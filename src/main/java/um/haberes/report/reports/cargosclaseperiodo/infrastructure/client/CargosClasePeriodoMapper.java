package um.haberes.report.reports.cargosclaseperiodo.infrastructure.client;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoDefinicion;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoDetalle;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoVigencia;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDetalleDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClasePeriodoDto;

@Component
public class CargosClasePeriodoMapper {

    public CargoClasePeriodoDetalle toDetalle(CargoClaseDetalleDto dto) {
        if (dto == null) {
            return null;
        }
        return new CargoClasePeriodoDetalle(dto.getLegajoId(), dto.getCargoClaseId(),
                dto.getGeograficaId());
    }

    public CargoClasePeriodoVigencia toVigencia(CargoClasePeriodoDto dto) {
        if (dto == null) {
            return null;
        }
        return new CargoClasePeriodoVigencia(dto.getCargoClaseId(), dto.getDescripcion(),
                dto.getPeriodoDesde(), dto.getPeriodoHasta(), dto.getHoras());
    }

    public CargoClasePeriodoDefinicion toDefinicion(CargoClaseDto dto) {
        if (dto == null) {
            return null;
        }
        return new CargoClasePeriodoDefinicion(dto.getCargoClaseId(), dto.getNombre(),
                dto.getClaseId());
    }
}
