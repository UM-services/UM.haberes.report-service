package um.haberes.report.reports.docentessede.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record DocentesSedeResponse(Integer anho, Integer mes, String facultad, String geografica,
                                   List<Curso> cursos) {

    public DocentesSedeResponse {
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
