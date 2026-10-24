package um.haberes.report.reports.comparacionremuneraciones.domain.model;

import java.math.BigDecimal;

/** Totales de la liquidacion acreditada de un legajo. */
public record LiquidacionRemuneracion(Long legajoId, BigDecimal totalRemunerativo,
                                      BigDecimal totalNeto) {
}
