package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.LegajoCursoCantidadResumen;

import java.util.List;

public interface LegajoCursoCantidadCatalogPort {

    List<LegajoCursoCantidadResumen> findAllByPeriodo(Integer anho, Integer mes);
}
