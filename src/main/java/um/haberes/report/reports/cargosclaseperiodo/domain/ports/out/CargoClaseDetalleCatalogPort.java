package um.haberes.report.reports.cargosclaseperiodo.domain.ports.out;

import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoDetalle;

import java.util.List;

public interface CargoClaseDetalleCatalogPort {

    List<CargoClasePeriodoDetalle> findByLegajo(Long legajoId, Integer anho, Integer mes);
}
