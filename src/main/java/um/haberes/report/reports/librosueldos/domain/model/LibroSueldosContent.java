package um.haberes.report.reports.librosueldos.domain.model;

import java.util.List;

/**
 * Libro de sueldos: totales de la liquidación del período por legajo. No replica el PDF legal
 * del sistema previo (Crystal no migrado); la tabla, el PDF propio y el XLSX salen de acá y el
 * ZIP del Libro de Sueldos Digital se pide aparte a haberes-core.
 */
public record LibroSueldosContent(Integer anho, Integer mes, List<Fila> filas) {

    public LibroSueldosContent {
        filas = List.copyOf(filas);
    }

    public record Fila(String legajoId, String apellidoNombre, String cuil, String dependencia,
                       String remunerativo, String noRemunerativo, String deducciones, String neto) {
    }
}
