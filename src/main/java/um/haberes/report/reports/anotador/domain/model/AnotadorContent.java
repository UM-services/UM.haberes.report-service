package um.haberes.report.reports.anotador.domain.model;

import java.util.List;

/** Contenido ya calculado del anotador. En pendientes, {@code rechazados} siempre esta vacio. */
public record AnotadorContent(Integer anho, Integer mes, boolean pendientes,
                              List<Registro> registros, List<Registro> rechazados) {

    public AnotadorContent {
        registros = List.copyOf(registros);
        rechazados = List.copyOf(rechazados);
    }

    public record Registro(String legajoId, String documento, String apellidoNombre,
                           String facultad, String fecha, String user, String estado,
                           String anotacion, String respuesta) {
    }
}
