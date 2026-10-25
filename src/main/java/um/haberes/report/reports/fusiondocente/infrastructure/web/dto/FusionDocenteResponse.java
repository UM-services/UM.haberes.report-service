package um.haberes.report.reports.fusiondocente.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record FusionDocenteResponse(Integer anho, Integer mes, List<Fila> filas) {

    public FusionDocenteResponse {
        filas = List.copyOf(filas);
    }

    public record Fila(String legajoId, String apellidoNombre, String facultad, String sede,
                       String cargo, String basico) {
    }
}
