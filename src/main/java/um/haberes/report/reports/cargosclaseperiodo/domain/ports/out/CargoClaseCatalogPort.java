package um.haberes.report.reports.cargosclaseperiodo.domain.ports.out;

import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoDefinicion;

import java.util.Optional;

public interface CargoClaseCatalogPort {

    Optional<CargoClasePeriodoDefinicion> findByCargoClaseId(Long cargoClaseId);
}
