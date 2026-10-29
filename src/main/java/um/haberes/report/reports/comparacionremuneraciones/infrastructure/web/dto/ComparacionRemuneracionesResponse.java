package um.haberes.report.reports.comparacionremuneraciones.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record ComparacionRemuneracionesResponse(Integer anho, Integer mes, Integer anhoAnterior,
                                                Integer mesAnterior, List<Dependencia> dependencias) {

    public ComparacionRemuneracionesResponse {
        dependencias = List.copyOf(dependencias);
    }

    public record Dependencia(String nombre, List<Fila> filas) {

        public Dependencia {
            filas = List.copyOf(filas);
        }
    }

    public record Fila(String legajoId, String apellidoNombre, String brutoAnterior,
                       String brutoActual, String brutoDiferencia, String brutoPorcentaje,
                       String netoAnterior, String netoActual, String netoDiferencia) {
    }
}
