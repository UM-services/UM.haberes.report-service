package um.haberes.report.reports.cursoscontratado.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record CursosContratadoResponse(Integer anho, Integer mes, List<Fila> filas) {

    public CursosContratadoResponse {
        filas = List.copyOf(filas);
    }

    public record Fila(String cuit, String apellidoNombre, String periodo, String facultad,
                       String sede, String curso, String cargo, String horas, String dictado) {
    }
}
