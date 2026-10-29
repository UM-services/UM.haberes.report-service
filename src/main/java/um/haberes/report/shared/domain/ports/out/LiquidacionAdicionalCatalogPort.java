package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.LiquidacionAdicionalResumen;

import java.util.List;

public interface LiquidacionAdicionalCatalogPort {

    List<LiquidacionAdicionalResumen> findAllByPeriodo(Integer anho, Integer mes);
}
