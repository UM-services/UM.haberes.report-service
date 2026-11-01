package um.haberes.report.reports.cargosclase.domain.ports.out;

import um.haberes.report.reports.cargosclase.domain.model.CargoClase;

import java.util.List;

public interface CargoClaseCatalogPort {

    List<CargoClase> findAll();
}
