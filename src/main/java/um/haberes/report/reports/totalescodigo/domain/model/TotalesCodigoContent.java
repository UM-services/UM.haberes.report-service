package um.haberes.report.reports.totalescodigo.domain.model;

import java.util.List;

public record TotalesCodigoContent(List<Fila> filas) {
    public TotalesCodigoContent {
        filas = List.copyOf(filas);
    }

    public record Fila(Integer codigoId, String nombre, String novedades, String liquidado, String diferencia) {
    }
}
