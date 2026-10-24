package um.haberes.report.reports.designacionesdetalle.domain.ports.out;

import java.math.BigDecimal;
import java.util.Optional;

/** Diferencia de designaciones (novedad de codigo 27) del legajo y periodo. */
public interface NovedadDiferenciaPort {

    Optional<BigDecimal> findImporte(Long legajoId, Integer anho, Integer mes,
                                     Integer codigoId, String dependenciaId);
}
