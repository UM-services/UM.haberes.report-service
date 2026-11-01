package um.haberes.report.reports.totalesgenerales.domain.model;

import java.math.BigDecimal;

/** Total mensual de un codigo para el periodo consultado. */
public record TotalMensualCodigo(Integer codigoId, BigDecimal total) {
}
