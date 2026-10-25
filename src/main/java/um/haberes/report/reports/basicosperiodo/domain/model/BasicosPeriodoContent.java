package um.haberes.report.reports.basicosperiodo.domain.model;

import java.util.List;

/**
 * Básicos por período: mezcla, por legajo liquidado, tres fuentes del sistema previo —cargos
 * liquidados, cargos con clase y adicionales—. Cada fuente emite sus propias filas, en ese
 * orden, igual que la planilla original.
 */
public record BasicosPeriodoContent(Integer anho, Integer mes, List<Fila> filas) {

    public BasicosPeriodoContent {
        filas = List.copyOf(filas);
    }

    public record Fila(String legajoId, String apellidoNombre, String dependencia, String claseCodigo,
                       String categoriaCargo, String basico, String horasJornada, String jornada) {
    }
}
