package um.haberes.report.reports.designacionesdocente.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record DesignacionesDocenteResponse(Integer anho, Integer mes, List<Docente> docentes) {

    public DesignacionesDocenteResponse {
        docentes = List.copyOf(docentes);
    }

    public record Docente(Long legajoId, String apellidoNombre, List<Fila> filas) {

        public Docente {
            filas = List.copyOf(filas);
        }
    }

    public record Fila(String facultad, String sede, String espacio, String horas, String cargo,
                       String designacion, String horasDesignacion, String dictado) {
    }
}
