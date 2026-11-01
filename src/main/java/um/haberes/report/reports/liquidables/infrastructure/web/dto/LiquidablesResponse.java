package um.haberes.report.reports.liquidables.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record LiquidablesResponse(List<Fila> filas) {

    public LiquidablesResponse {
        filas = List.copyOf(filas);
    }

    public record Fila(String legajoId, String apellidoNombre, String estado,
                       String dependenciaId, String dependenciaNombre) {
    }
}
