package um.haberes.report.reports.totalesgenerales.domain.model;

/** Pertenencia de un codigo a los grupos fijos del reporte. */
public record CodigoGrupo(Integer codigoId, boolean remunerativo, boolean noRemunerativo,
                          boolean deduccion, boolean total) {
}
