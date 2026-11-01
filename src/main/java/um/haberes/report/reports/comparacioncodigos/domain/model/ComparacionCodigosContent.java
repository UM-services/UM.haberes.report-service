package um.haberes.report.reports.comparacioncodigos.domain.model;

import java.util.List;

/** Filas ya formateadas del reporte de comparacion de codigos. */
public record ComparacionCodigosContent(List<Fila> filas) {

    public ComparacionCodigosContent {
        filas = List.copyOf(filas);
    }

    public record Fila(Integer codigoId, String nombre, String novedades, String liquidado, String diferencia) {
    }
}
