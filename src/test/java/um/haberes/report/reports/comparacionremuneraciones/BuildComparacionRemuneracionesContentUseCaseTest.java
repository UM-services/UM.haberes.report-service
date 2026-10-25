package um.haberes.report.reports.comparacionremuneraciones;

import org.junit.jupiter.api.Test;
import um.haberes.report.reports.comparacionremuneraciones.application.usecases.BuildComparacionRemuneracionesContentUseCaseImpl;
import um.haberes.report.reports.comparacionremuneraciones.domain.model.ComparacionRemuneracionesContent;
import um.haberes.report.reports.comparacionremuneraciones.domain.model.LiquidacionRemuneracion;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.out.LiquidacionAcreditadoCatalogPort;
import um.haberes.report.shared.domain.model.DependenciaResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.ports.out.DependenciaCatalogPort;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Contenido compartido de "Comparación de Remuneraciones": agrupación, orden y cálculos. */
class BuildComparacionRemuneracionesContentUseCaseTest {

    private final LiquidacionAcreditadoCatalogPort liquidacionPort =
            mock(LiquidacionAcreditadoCatalogPort.class);
    private final PersonaCatalogPort personaCatalogPort = mock(PersonaCatalogPort.class);
    private final DependenciaCatalogPort dependenciaCatalogPort = mock(DependenciaCatalogPort.class);
    private final BuildComparacionRemuneracionesContentUseCaseImpl useCase =
            new BuildComparacionRemuneracionesContentUseCaseImpl(liquidacionPort, personaCatalogPort,
                    dependenciaCatalogPort);

    @Test
    void ordenaDependenciasYPersonasYCalculaDiferencias() {
        when(liquidacionPort.findAllByAcreditado(2026, 9)).thenReturn(List.of(
                new LiquidacionRemuneracion(1002L, new BigDecimal("800000"), new BigDecimal("650000")),
                new LiquidacionRemuneracion(1001L, new BigDecimal("1500000"), new BigDecimal("1200000")),
                new LiquidacionRemuneracion(1003L, new BigDecimal("950000.50"), new BigDecimal("700000.25"))));
        when(liquidacionPort.findAllByAcreditado(2026, 8)).thenReturn(List.of(
                new LiquidacionRemuneracion(1001L, new BigDecimal("1400000"), new BigDecimal("1100000"))));
        when(personaCatalogPort.findAll()).thenReturn(List.of(
                persona(1002L, "Pérez", "Juan", 20),
                persona(1001L, "Gómez", "Ana", 20),
                persona(1003L, "López", "María", 10),
                persona(1004L, "Díaz", "Carlos", 10)));
        when(dependenciaCatalogPort.findAll()).thenReturn(List.of(
                dependencia(20, "Facultad de Ingeniería"), dependencia(10, "Rectorado")));

        ComparacionRemuneracionesContent content = useCase.build(2026, 9, 2026, 8);

        assertThat(content.dependencias()).extracting(ComparacionRemuneracionesContent.Dependencia::nombre)
                .containsExactly("Rectorado", "Facultad de Ingeniería");
        assertThat(content.dependencias().get(0).filas()).extracting(ComparacionRemuneracionesContent.Fila::apellidoNombre)
                .containsExactly("López, María");
        assertThat(content.dependencias().get(1).filas()).extracting(ComparacionRemuneracionesContent.Fila::apellidoNombre)
                .containsExactly("Gómez, Ana", "Pérez, Juan");
        assertThat(content.dependencias().get(1).filas().get(0).brutoPorcentaje()).isEqualTo("7,14%");
        assertThat(content.dependencias().get(1).filas().get(0).netoDiferencia()).isEqualTo("100.000");
    }

    @Test
    void personaSinLiquidacionAnteriorSeComparaContraCeroYPersonaSinActualSeOmite() {
        when(liquidacionPort.findAllByAcreditado(2026, 9)).thenReturn(List.of(
                new LiquidacionRemuneracion(1002L, new BigDecimal("950000.50"), new BigDecimal("700000.25"))));
        when(liquidacionPort.findAllByAcreditado(2026, 8)).thenReturn(List.of());
        when(personaCatalogPort.findAll()).thenReturn(List.of(
                persona(1001L, "Sin", "Actual", 10),
                persona(1002L, "Nueva", "Liquidacion", 10)));
        when(dependenciaCatalogPort.findAll()).thenReturn(List.of(dependencia(10, "Rectorado")));

        ComparacionRemuneracionesContent content = useCase.build(2026, 9, 2026, 8);

        List<ComparacionRemuneracionesContent.Fila> filas = content.dependencias().get(0).filas();
        assertThat(filas).singleElement();
        assertThat(filas.get(0).brutoAnterior()).isEqualTo("0");
        assertThat(filas.get(0).brutoPorcentaje()).isEqualTo("0%");
        assertThat(filas.get(0).netoDiferencia()).isEqualTo("700.000,25");
    }

    @Test
    void sinLiquidacionesNoGeneraDependencias() {
        when(liquidacionPort.findAllByAcreditado(2026, 9)).thenReturn(List.of());
        when(liquidacionPort.findAllByAcreditado(2026, 8)).thenReturn(List.of());
        when(personaCatalogPort.findAll()).thenReturn(List.of());
        when(dependenciaCatalogPort.findAll()).thenReturn(List.of());

        ComparacionRemuneracionesContent content = useCase.build(2026, 9, 2026, 8);

        assertThat(content.dependencias()).isEmpty();
    }

    private static PersonaResumen persona(Long legajoId, String apellido, String nombre,
                                          Integer dependenciaId) {
        return PersonaResumen.builder().legajoId(legajoId).apellido(apellido).nombre(nombre)
                .dependenciaId(dependenciaId).build();
    }

    private static DependenciaResumen dependencia(Integer id, String nombre) {
        return DependenciaResumen.builder().dependenciaId(id).nombre(nombre).build();
    }
}
