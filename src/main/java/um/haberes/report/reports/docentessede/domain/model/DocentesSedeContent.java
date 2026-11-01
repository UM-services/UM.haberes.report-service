package um.haberes.report.reports.docentessede.domain.model;

import java.util.List;

/** Contenido ya calculado: un bloque por curso de la sede. */
public record DocentesSedeContent(Integer anho, Integer mes, String facultad, String geografica,
                                  List<Curso> cursos) {

    public DocentesSedeContent {
        cursos = List.copyOf(cursos);
    }

    public record Curso(String nombre, String periodo, List<Fila> filas) {

        public Curso {
            filas = List.copyOf(filas);
        }
    }

    public record Fila(String cargo, String docente, String designacion, String horas,
                       String desarraigo) {
    }
}
