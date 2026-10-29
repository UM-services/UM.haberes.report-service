package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.CargoClaseDetalleResumen;

import java.util.List;

public interface CargoClaseDetalleCatalogPort {

    List<CargoClaseDetalleResumen> findAllByPeriodo(Integer anho, Integer mes);
}
