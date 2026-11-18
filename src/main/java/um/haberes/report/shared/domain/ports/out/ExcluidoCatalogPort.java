package um.haberes.report.shared.domain.ports.out;

import java.util.List;

import um.haberes.report.shared.domain.model.ExcluidoResumen;

public interface ExcluidoCatalogPort {

    List<ExcluidoResumen> findAllByPeriodo(Integer anho, Integer mes);
}
