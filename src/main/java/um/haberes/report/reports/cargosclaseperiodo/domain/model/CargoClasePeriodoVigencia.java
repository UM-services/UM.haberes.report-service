package um.haberes.report.reports.cargosclaseperiodo.domain.model;

/** Vigencia declarada de un cargo con clase para un legajo. */
public record CargoClasePeriodoVigencia(Long cargoClaseId, String descripcion,
                                       Long periodoDesde, Long periodoHasta, Integer horas) {
}
