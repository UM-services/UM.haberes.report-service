package um.haberes.report.reports.comparacionremuneraciones.domain.ports.out;

import um.haberes.report.reports.comparacionremuneraciones.domain.model.LiquidacionRemuneracion;

import java.util.List;

public interface LiquidacionAcreditadoCatalogPort {

    List<LiquidacionRemuneracion> findAllByAcreditado(Integer anho, Integer mes);
}
