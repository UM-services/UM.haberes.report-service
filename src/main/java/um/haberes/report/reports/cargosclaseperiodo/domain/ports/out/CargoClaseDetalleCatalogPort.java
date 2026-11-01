package um.haberes.report.reports.cargosclaseperiodo.domain.ports.out;

import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoDetalle;

import java.util.List;

public interface CargoClaseDetalleCatalogPort {

    List<CargoClasePeriodoDetalle> findByLegajo(Long legajoId, Integer anho, Integer mes);

    /** Todos los detalles de la facultad en el período, en una sola llamada. */
    List<CargoClasePeriodoDetalle> findAllByFacultad(Integer facultadId, Integer anho, Integer mes);
}
