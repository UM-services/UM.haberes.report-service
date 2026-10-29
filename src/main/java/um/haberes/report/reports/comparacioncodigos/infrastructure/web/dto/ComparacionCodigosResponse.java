package um.haberes.report.reports.comparacioncodigos.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record ComparacionCodigosResponse(Integer anho, Integer mes, List<Fila> filas) {

    public ComparacionCodigosResponse {
        filas = List.copyOf(filas);
    }

    public record Fila(Integer codigoId, String nombre, String novedades, String liquidado,
                       String diferencia) {
    }
}
