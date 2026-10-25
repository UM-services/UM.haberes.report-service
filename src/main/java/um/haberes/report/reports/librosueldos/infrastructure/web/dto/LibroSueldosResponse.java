package um.haberes.report.reports.librosueldos.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record LibroSueldosResponse(Integer anho, Integer mes, List<Fila> filas) {

    public LibroSueldosResponse {
        filas = List.copyOf(filas);
    }

    public record Fila(String legajoId, String apellidoNombre, String cuil, String dependencia,
                       String remunerativo, String noRemunerativo, String deducciones, String neto) {
    }
}
