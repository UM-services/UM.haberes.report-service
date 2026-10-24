package um.haberes.report.reports.comparacionremuneraciones.domain.model;

import java.util.List;

/** Contenido ya calculado: por dependencia, una fila por legajo comparado. */
public record ComparacionRemuneracionesContent(Integer anho, Integer mes,
                                               Integer anhoAnterior, Integer mesAnterior,
                                               List<Dependencia> dependencias) {

    public ComparacionRemuneracionesContent {
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
