package um.haberes.report.reports.sheet.domain.model;

import java.math.BigDecimal;
import java.util.List;

/** Contenido ya calculado de la hoja de cursos por docente. */
public record SheetCursosContent(Integer anho, Integer mes, List<Fila> filas) {

    public SheetCursosContent {
        filas = List.copyOf(filas);
    }

    public record Fila(Long legajoId, String apellidoNombre, String periodo, Integer facultadId,
                       String facultad, Integer geograficaId, String geografica, Long cursoId,
                       String curso, Integer cargoTipoId, String cargo, BigDecimal horas,
                       String dictado) {
    }
}
