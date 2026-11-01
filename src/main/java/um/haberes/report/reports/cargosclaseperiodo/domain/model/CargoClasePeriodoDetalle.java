package um.haberes.report.reports.cargosclaseperiodo.domain.model;

/** Asignacion de un cargo con clase a un legajo en el periodo consultado. */
public record CargoClasePeriodoDetalle(Long legajoId, Long cargoClaseId, Integer geograficaId) {
}
