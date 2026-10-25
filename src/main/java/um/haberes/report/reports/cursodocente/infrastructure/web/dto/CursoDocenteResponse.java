package um.haberes.report.reports.cursodocente.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record CursoDocenteResponse(Integer anho, Integer mes, List<Docente> docentes) {

    public CursoDocenteResponse {
        docentes = List.copyOf(docentes);
    }

    public record Docente(Long legajoId, String apellidoNombre, List<Fila> filas) {

        public Docente {
            filas = List.copyOf(filas);
        }
    }

    public record Fila(String facultad, String sede, String espacio, String horas, String cargo,
                       String basico, String anual, String semestre1, String semestre2) {
    }
}
