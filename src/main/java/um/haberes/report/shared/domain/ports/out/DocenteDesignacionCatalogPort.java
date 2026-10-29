package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.DocenteDesignacionResumen;

import java.util.List;

public interface DocenteDesignacionCatalogPort {

    List<DocenteDesignacionResumen> findAllByPeriodo(Integer anho, Integer mes);
}
