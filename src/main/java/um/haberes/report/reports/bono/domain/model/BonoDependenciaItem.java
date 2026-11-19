package um.haberes.report.reports.bono.domain.model;

/**
 * Un legajo del lote con sus datos ya resueltos. El legajoId viaja aparte de la persona porque
 * un legajo sin persona también debe poder informarse como fallido.
 */
public record BonoDependenciaItem(Long legajoId, BonoFuente fuente) {
}
