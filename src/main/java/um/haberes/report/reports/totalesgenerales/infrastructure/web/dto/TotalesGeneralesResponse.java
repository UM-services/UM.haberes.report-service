package um.haberes.report.reports.totalesgenerales.infrastructure.web.dto;

import java.util.List;

/**
 * Contrato HTTP del reporte para la tabla del frontend. Es un DTO propio del slice (no expone
 * el modelo de dominio) y refleja exactamente el contenido que se imprime en el PDF y el XLSX.
 */
public record TotalesGeneralesResponse(Integer anho, Integer mes, List<Grupo> grupos) {

    public TotalesGeneralesResponse {
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
