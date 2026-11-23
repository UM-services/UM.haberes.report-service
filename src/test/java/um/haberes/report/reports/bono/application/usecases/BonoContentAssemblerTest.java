package um.haberes.report.reports.bono.application.usecases;

import org.junit.jupiter.api.Test;
import um.haberes.report.reports.bono.domain.model.BonoContent;
import um.haberes.report.reports.bono.domain.model.BonoFuente;
import um.haberes.report.reports.bono.domain.model.DetalleCargosContent;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regla de horas de la tabla de cursos: en el recibo sólo se muestran en los cursos de la
 * Escuela Técnica (facultad 15); el detalle de cargos las muestra siempre.
 */
class BonoContentAssemblerTest {

    private static final int ANHO = 2026;
    private static final int MES = 9;
    private static final long LEGAJO = 120L;

    private final BonoContentAssembler assembler = new BonoContentAssembler();

    @Test
    void elReciboMuestraLasHorasDeUnCursoEtec() {
        BonoContent content = assembler.recibo(fuente(curso(15, "6")), ANHO, MES).content();

        assertThat(content.cursos()).singleElement()
                .extracting(BonoContent.Curso::horas).isEqualTo("6");
    }

    @Test
    void elReciboOcultaLasHorasDeUnCursoDeOtraFacultad() {
        BonoContent content = assembler.recibo(fuente(curso(2, "6")), ANHO, MES).content();

        assertThat(content.cursos()).singleElement()
                .extracting(BonoContent.Curso::horas).isEqualTo("");
    }

    @Test
    void elReciboOcultaLasHorasSiElCursoNoTraeFacultad() {
        BonoContent content = assembler.recibo(fuente(curso(null, "6")), ANHO, MES).content();

        assertThat(content.cursos()).singleElement()
                .extracting(BonoContent.Curso::horas).isEqualTo("");
    }

    @Test
    void elDetalleDeCargosSigueMostrandoLasHorasDeCualquierFacultad() {
        DetalleCargosContent content = assembler.detalle(fuente(curso(2, "6")), LEGAJO, 2);

        assertThat(content.cursos()).singleElement()
                .extracting(BonoContent.Curso::horas).isEqualTo("6");
    }

    private BonoFuente.CursoCargo curso(Integer facultadId, String horasSemanales) {
        return new BonoFuente.CursoCargo("Profesor Titular", "Análisis Matemático I", facultadId,
                (byte) 1, (byte) 1, (byte) 0, (byte) 0, new BigDecimal(horasSemanales));
    }

    private BonoFuente fuente(BonoFuente.CursoCargo curso) {
        OffsetDateTime fecha = OffsetDateTime.parse("2026-09-01T00:00:00Z");
        return new BonoFuente(
                new BonoFuente.Persona(LEGAJO, "Gómez", "Ana", "30111222", "20-30111222-3",
                        "FI", null),
                new BonoFuente.Control(fecha, fecha, fecha, fecha, "11%"),
                new BonoFuente.Antiguedad(10, 0),
                List.of(BigDecimal.ONE),
                new BonoFuente.Banco("0000000000000000000000"),
                List.of(new BonoFuente.Item(1, new BigDecimal("1000")),
                        new BonoFuente.Item(99, new BigDecimal("800"))),
                List.of(curso),
                List.of(),
                List.of(), List.of(), List.of(), List.of(), List.of(),
                null,
                List.of(), List.of(), List.of(),
                null, null, 1L, false, "Facultad de Ingeniería");
    }
}
