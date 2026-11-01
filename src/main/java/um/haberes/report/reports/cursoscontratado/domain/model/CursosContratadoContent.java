package um.haberes.report.reports.cursoscontratado.domain.model;

import java.util.List;

/**
 * Cursos por contratado del período: una fila por curso-cargo de cada contrato, con la persona,
 * la facultad, la sede, el curso, el cargo, las horas y el tipo de dictado.
 */
public record CursosContratadoContent(Integer anho, Integer mes, List<Fila> filas) {

    public CursosContratadoContent {
        filas = List.copyOf(filas);
    }

    public record Fila(String cuit, String apellidoNombre, String periodo, String facultad,
                       String sede, String curso, String cargo, String horas, String dictado) {
    }
}
