package um.haberes.report.reports.excluidos.domain.model;

import java.util.List;

/**
 * Legajos excluidos de la acreditación del período (legacy: {@code rptExcluidos}).
 *
 * <p>El diseño del {@code .rpt} original es binario (Crystal) y no está disponible en el
 * repositorio; la planilla expone las columnas del ABM más las observaciones y la fecha de alta,
 * que son los datos que el legacy muestra al imprimir.
 */
public record ExcluidosContent(Integer anho, Integer mes, List<Fila> filas) {

    public ExcluidosContent {
        filas = List.copyOf(filas);
    }

    public record Fila(String legajoId, String apellidoNombre, String importe, String observaciones,
                       String fecha) {
    }
}
