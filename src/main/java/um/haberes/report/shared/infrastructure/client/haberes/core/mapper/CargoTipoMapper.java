package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CargoTipoResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoTipoDto;

@Component
public class CargoTipoMapper {

    public CargoTipoResumen toDomain(CargoTipoDto dto) {
        if (dto == null) {
            return null;
        }
        return CargoTipoResumen.builder()
                .cargoTipoId(dto.getCargoTipoId())
                .nombre(dto.getNombre())
                .build();
    }
}
