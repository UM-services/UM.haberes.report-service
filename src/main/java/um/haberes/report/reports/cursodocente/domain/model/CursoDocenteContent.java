package um.haberes.report.reports.cursodocente.domain.model;

import java.util.List;

/** Contenido ya calculado: un bloque por docente con sus cursos cargo. */
public record CursoDocenteContent(Integer anho, Integer mes, List<Docente> docentes) {

    public CursoDocenteContent {
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
