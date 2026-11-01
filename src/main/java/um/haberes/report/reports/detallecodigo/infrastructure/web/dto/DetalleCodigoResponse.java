package um.haberes.report.reports.detallecodigo.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record DetalleCodigoResponse(String codigoId, String codigoNombre, List<Fila> filas) {

    public DetalleCodigoResponse {
        filas = List.copyOf(filas);
    }

    public record Fila(String legajoId, String apellidoNombre, String liquidado, String novedad,
                       String cantidad) {
    }
}
