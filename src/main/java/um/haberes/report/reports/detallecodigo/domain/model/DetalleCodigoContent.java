package um.haberes.report.reports.detallecodigo.domain.model;

import java.util.List;

/**
 * Detalle por código: una fila por persona liquidada en el período, con el importe liquidado
 * del código y el acumulado de novedades (importe y cantidad). El código se identifica en el
 * encabezado, no en cada fila.
 */
public record DetalleCodigoContent(Integer codigoId, String codigoNombre, Integer anho, Integer mes,
                                   List<Fila> filas) {

    public DetalleCodigoContent {
        filas = List.copyOf(filas);
    }

    public record Fila(String legajoId, String apellidoNombre, String liquidado, String novedad,
                       Integer cantidad) {
    }
}
