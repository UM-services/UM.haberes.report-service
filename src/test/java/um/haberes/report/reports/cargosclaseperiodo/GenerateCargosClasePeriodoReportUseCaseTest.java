package um.haberes.report.reports.cargosclaseperiodo;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import um.haberes.report.reports.cargosclaseperiodo.application.usecases.GenerateCargosClasePeriodoReportUseCaseImpl;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoDefinicion;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoDetalle;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoVigencia;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargosClasePeriodoContent;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.out.CargoClaseCatalogPort;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.out.CargoClaseDetalleCatalogPort;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.out.CargoClasePeriodoCatalogPort;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.out.CargosClasePeriodoPdfRenderer;
import um.haberes.report.shared.domain.model.ClaseResumen;
import um.haberes.report.shared.domain.model.FacultadResumen;
import um.haberes.report.shared.domain.model.GeograficaResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ClaseCatalogPort;
import um.haberes.report.shared.domain.ports.out.FacultadCatalogPort;
import um.haberes.report.shared.domain.ports.out.GeograficaCatalogPort;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Caso de uso de "Cargos con Clase por Periodo": agrupacion por sede, orden y omisiones. */
class GenerateCargosClasePeriodoReportUseCaseTest {

    private static final Path TARGET = Path.of("target/test-output/cargos-clase-periodo..2.20269..pdf");

    private final PersonaCatalogPort personaCatalogPort = mock(PersonaCatalogPort.class);
    private final FacultadCatalogPort facultadCatalogPort = mock(FacultadCatalogPort.class);
    private final GeograficaCatalogPort geograficaCatalogPort = mock(GeograficaCatalogPort.class);
    private final ClaseCatalogPort claseCatalogPort = mock(ClaseCatalogPort.class);
    private final CargoClaseDetalleCatalogPort detalleCatalogPort = mock(CargoClaseDetalleCatalogPort.class);
    private final CargoClasePeriodoCatalogPort vigenciaCatalogPort = mock(CargoClasePeriodoCatalogPort.class);
    private final CargoClaseCatalogPort cargoClaseCatalogPort = mock(CargoClaseCatalogPort.class);
    private final CargosClasePeriodoPdfRenderer renderer = mock(CargosClasePeriodoPdfRenderer.class);
    private final ReportStorage storage = mock(ReportStorage.class);
    private final GenerateCargosClasePeriodoReportUseCaseImpl useCase =
            new GenerateCargosClasePeriodoReportUseCaseImpl(personaCatalogPort, facultadCatalogPort,
                    geograficaCatalogPort, claseCatalogPort, detalleCatalogPort, vigenciaCatalogPort,
                    cargoClaseCatalogPort, renderer, storage);

    @Test
    void agrupaPorSedeEnOrdenAlfabeticoYOmiteDatosIncompletos() {
        stubStorage();
        when(facultadCatalogPort.findById(2))
                .thenReturn(Optional.of(FacultadResumen.builder().facultadId(2).nombre("Facultad de Ingeniería").build()));
        when(personaCatalogPort.findAllByFacultad(2)).thenReturn(List.of(persona(1001L, "Gómez", "Ana")));
        when(geograficaCatalogPort.findAll()).thenReturn(List.of(
                geografica(1, "Campus Universitario"), geografica(2, "Sede Centro"),
                geografica(3, "Sede San Rafael")));
        when(claseCatalogPort.findAll()).thenReturn(List.of(clase(1, "Primera"), clase(2, "Segunda")));
        when(detalleCatalogPort.findByLegajo(1001L, 2026, 9)).thenReturn(List.of(
                new CargoClasePeriodoDetalle(1001L, 501L, 3),
                new CargoClasePeriodoDetalle(1001L, 502L, 1),
                new CargoClasePeriodoDetalle(1001L, 504L, 1),
                new CargoClasePeriodoDetalle(1001L, 505L, 1)));
        when(vigenciaCatalogPort.findAllByLegajo(1001L)).thenReturn(List.of(
                new CargoClasePeriodoVigencia(501L, "Cargo ordinario", 202601L, 202612L, 10),
                new CargoClasePeriodoVigencia(502L, "Licencia parcial", 202603L, 202609L, 6)));
        when(cargoClaseCatalogPort.findByCargoClaseId(501L)).thenReturn(Optional.of(
                new CargoClasePeriodoDefinicion(501L, "Profesor Titular", 1)));
        when(cargoClaseCatalogPort.findByCargoClaseId(502L)).thenReturn(Optional.of(
                new CargoClasePeriodoDefinicion(502L, "Jefe de Trabajos Prácticos", 2)));
        when(cargoClaseCatalogPort.findByCargoClaseId(505L)).thenReturn(Optional.empty());

        useCase.generate(2, 2026, 9);

        ArgumentCaptor<CargosClasePeriodoContent> captor =
                ArgumentCaptor.forClass(CargosClasePeriodoContent.class);
        verify(renderer).render(captor.capture(), eq(TARGET));
        CargosClasePeriodoContent content = captor.getValue();

        assertThat(content.facultad()).isEqualTo("Facultad de Ingeniería");
        assertThat(content.sedes()).extracting(CargosClasePeriodoContent.Sede::nombre)
                .containsExactly("Campus Universitario", "Sede San Rafael");
        assertThat(content.sedes().get(0).filas()).singleElement()
                .satisfies(fila -> {
                    assertThat(fila.cargo()).isEqualTo("Jefe de Trabajos Prácticos");
                    assertThat(fila.clase()).isEqualTo("Segunda");
                    assertThat(fila.horas()).isEqualTo(6);
                    assertThat(fila.apellidoNombre()).isEqualTo("Gómez, Ana");
                });
        assertThat(content.sedes().get(1).filas()).singleElement()
                .satisfies(fila -> assertThat(fila.cargo()).isEqualTo("Profesor Titular"));
        verify(storage).load(TARGET, "cargos-clase-periodo.pdf", ReportFile.Format.PDF);
    }

    @Test
    void sinPersonasNoGeneraSedes() {
        stubStorage();
        when(facultadCatalogPort.findById(2))
                .thenReturn(Optional.of(FacultadResumen.builder().facultadId(2).nombre("FI").build()));
        when(personaCatalogPort.findAllByFacultad(2)).thenReturn(List.of());
        when(geograficaCatalogPort.findAll()).thenReturn(List.of());
        when(claseCatalogPort.findAll()).thenReturn(List.of());

        useCase.generate(2, 2026, 9);

        ArgumentCaptor<CargosClasePeriodoContent> captor =
                ArgumentCaptor.forClass(CargosClasePeriodoContent.class);
        verify(renderer).render(captor.capture(), eq(TARGET));
        assertThat(captor.getValue().sedes()).isEmpty();
        assertThat(captor.getValue().facultad()).isEqualTo("FI");
    }

    @Test
    void facultadAusenteNoRompeElEncabezado() {
        stubStorage();
        when(facultadCatalogPort.findById(2)).thenReturn(Optional.empty());
        when(personaCatalogPort.findAllByFacultad(2)).thenReturn(List.of());
        when(geograficaCatalogPort.findAll()).thenReturn(List.of());
        when(claseCatalogPort.findAll()).thenReturn(List.of());

        useCase.generate(2, 2026, 9);

        ArgumentCaptor<CargosClasePeriodoContent> captor =
                ArgumentCaptor.forClass(CargosClasePeriodoContent.class);
        verify(renderer).render(captor.capture(), eq(TARGET));
        assertThat(captor.getValue().facultad()).isNull();
        assertThat(captor.getValue().sedes()).isEmpty();
    }

    private void stubStorage() {
        when(storage.resolve("cargos-clase-periodo..2.20269..pdf")).thenReturn(TARGET);
        when(storage.load(any(), any(), any())).thenReturn(
                new ReportFile(TARGET, "cargos-clase-periodo.pdf", ReportFile.Format.PDF, 100));
    }

    private static PersonaResumen persona(Long legajoId, String apellido, String nombre) {
        return PersonaResumen.builder().legajoId(legajoId).apellido(apellido).nombre(nombre).build();
    }

    private static GeograficaResumen geografica(Integer id, String nombre) {
        return GeograficaResumen.builder().geograficaId(id).nombre(nombre).build();
    }

    private static ClaseResumen clase(Integer id, String nombre) {
        return ClaseResumen.builder().claseId(id).nombre(nombre).build();
    }
}
