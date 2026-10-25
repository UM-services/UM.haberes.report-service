package um.haberes.report.reports.cargosperiodo.domain.model;

import java.util.List;

/**
 * Cargos activos del período por legajo. Slice de sólo lectura: no replica el borrado de
 * cargos no liquidados que hacía la planilla original antes de listar.
 */
public record CargosPeriodoContent(Integer anho, Integer mes, List<Fila> filas) {

    public CargosPeriodoContent {
        filas = List.copyOf(filas);
    }

    public record Fila(String legajoId, String apellidoNombre, String periodo, String dependenciaId,
                       String dependenciaNombre, String categoriaId, String categoriaNombre) {
    }
}
