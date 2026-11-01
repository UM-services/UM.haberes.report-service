package um.haberes.report.reports.cargosclase.infrastructure.client;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.cargosclase.domain.model.CargoClase;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDto;

@Component
public class CargoClaseMapper {

    public CargoClase toDomain(CargoClaseDto dto) {
        if (dto == null) {
            return null;
        }
        return new CargoClase(dto.getCargoClaseId(), dto.getNombre(), dto.getClaseId());
    }
}
