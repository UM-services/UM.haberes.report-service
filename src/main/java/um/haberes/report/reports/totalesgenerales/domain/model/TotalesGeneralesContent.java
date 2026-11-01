package um.haberes.report.reports.totalesgenerales.domain.model;

import java.util.List;

/** Contenido ya calculado: una tabla por grupo, en el orden fijo del reporte. */
public record TotalesGeneralesContent(Integer anho, Integer mes, List<Grupo> grupos) {

    public TotalesGeneralesContent {
        grupos = List.copyOf(grupos);
    }

    public record Grupo(String nombre, List<Fila> filas, String subtotal) {

        public Grupo {
            filas = List.copyOf(filas);
        }
    }

    public record Fila(String codigoId, String nombre, String total) {
    }
}
