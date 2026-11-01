package um.haberes.report.reports.liquidables.domain.model;

import java.util.List;

/**
 * Contenido ya calculado de la planilla de liquidables: una fila por persona con el flag
 * de liquidación activo. No depende de un período (la planilla sale del estado actual).
 */
public record LiquidablesContent(List<Fila> filas) {

    public LiquidablesContent {
        filas = List.copyOf(filas);
    }

    public record Fila(Long legajoId, String apellidoNombre, Integer estado,
                       Integer dependenciaId, String dependenciaNombre) {
    }
}
