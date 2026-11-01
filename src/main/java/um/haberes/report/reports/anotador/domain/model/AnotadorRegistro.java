package um.haberes.report.reports.anotador.domain.model;

import java.time.LocalDateTime;

/** Registro crudo del anotador, tal como lo entrega el core. */
public record AnotadorRegistro(Long legajoId, String documento, String apellidoNombre,
                               String facultad, LocalDateTime created, String user,
                               int autorizado, int rechazado, String anotacion,
                               String respuesta) {
}
