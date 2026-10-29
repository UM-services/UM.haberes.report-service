package um.haberes.report.reports.totalescodigo.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record TotalesCodigoResponse(Integer anho, Integer mes, List<Fila> filas) {

    public TotalesCodigoResponse {
        filas = List.copyOf(filas);
    }

    public record Fila(Integer codigoId, String nombre, String novedades, String liquidado,
                       String diferencia) {
    }
}
