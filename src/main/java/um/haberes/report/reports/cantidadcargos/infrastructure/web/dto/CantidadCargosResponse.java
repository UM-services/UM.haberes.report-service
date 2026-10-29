package um.haberes.report.reports.cantidadcargos.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record CantidadCargosResponse(Integer anho, Integer mes, List<Fila> filas) {

    public CantidadCargosResponse {
        filas = List.copyOf(filas);
    }

    public record Fila(String facultad, String sede, String legajoId, String apellidoNombre,
                       Integer anuales, Integer semestre1, Integer semestre2, String bruto) {
    }
}
