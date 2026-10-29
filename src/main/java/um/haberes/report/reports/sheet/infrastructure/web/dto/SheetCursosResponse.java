package um.haberes.report.reports.sheet.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record SheetCursosResponse(Integer anho, Integer mes, List<Fila> filas) {

    public SheetCursosResponse {
        filas = List.copyOf(filas);
    }

    public record Fila(String legajoId, String apellidoNombre, String periodo, String facultad,
                       String sede, String curso, String cargo, String horas, String dictado) {
    }
}
