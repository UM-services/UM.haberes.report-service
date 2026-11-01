package um.haberes.report.reports.designacionesdocente.domain.model;

import java.util.List;

/** Contenido ya calculado: un bloque por docente con sus designaciones. */
public record DesignacionesDocenteContent(Integer anho, Integer mes, List<Docente> docentes) {

    public DesignacionesDocenteContent {
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
