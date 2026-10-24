package um.haberes.report.reports.fusiondocente.domain.model;

import java.util.List;

/** Contenido ya calculado: una fila por curso fusionado del docente. */
public record FusionDocenteContent(Integer anho, Integer mes, List<Fila> filas) {

    public FusionDocenteContent {
        filas = List.copyOf(filas);
    }

    public record Fila(String legajoId, String apellidoNombre, String facultad, String sede,
                       String cargo, String basico) {
    }
}
