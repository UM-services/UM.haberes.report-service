package um.haberes.report.reports.cantidadcargos.domain.model;

import java.util.List;

/**
 * Contenido de la planilla de cantidad de cargos: una fila por legajo de la vista
 * {@code vw_legajo_curso_cantidad}, con el bruto liquidado del código 96.
 */
public record CantidadCargosContent(Integer anho, Integer mes, List<Fila> filas) {

    public CantidadCargosContent {
        filas = List.copyOf(filas);
    }

    public record Fila(String facultad, String sede, String legajoId, String apellidoNombre,
                       Integer anuales, Integer semestre1, Integer semestre2, String bruto) {
    }
}
