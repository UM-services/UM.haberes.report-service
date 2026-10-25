package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.CargoLiquidacionResumen;

import java.util.List;

public interface CargoLiquidacionCatalogPort {

    List<CargoLiquidacionResumen> findAllActivosByPeriodo(Integer anho, Integer mes);

    List<CargoLiquidacionResumen> findAllByPeriodo(Integer anho, Integer mes);

    List<CargoLiquidacionResumen> findAllDocenteByLegajo(Long legajoId, Integer anho, Integer mes);

    List<CargoLiquidacionResumen> findAllNoDocenteByLegajo(Long legajoId, Integer anho, Integer mes);

    List<CargoLiquidacionResumen> findAllDocenteByLegajoAndFacultad(
            Long legajoId, Integer anho, Integer mes, Integer facultadId);

    List<CargoLiquidacionResumen> findAllNoDocenteByLegajoAndFacultad(
            Long legajoId, Integer anho, Integer mes, Integer facultadId);
}
