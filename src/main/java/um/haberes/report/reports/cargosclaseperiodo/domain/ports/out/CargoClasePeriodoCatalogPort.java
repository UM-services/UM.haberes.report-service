package um.haberes.report.reports.cargosclaseperiodo.domain.ports.out;

import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoVigencia;

import java.util.List;

public interface CargoClasePeriodoCatalogPort {

    List<CargoClasePeriodoVigencia> findAllByLegajo(Long legajoId);
}
