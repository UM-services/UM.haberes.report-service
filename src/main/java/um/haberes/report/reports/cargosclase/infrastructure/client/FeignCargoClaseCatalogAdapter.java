package um.haberes.report.reports.cargosclase.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cargosclase.domain.model.CargoClase;
import um.haberes.report.reports.cargosclase.domain.ports.out.CargoClaseCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoClaseClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDto;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignCargoClaseCatalogAdapter implements CargoClaseCatalogPort {

    private final CargoClaseClient cargoClaseClient;
    private final CargoClaseMapper cargoClaseMapper;

    @Override
    public List<CargoClase> findAll() {
        List<CargoClaseDto> dtos = cargoClaseClient.findAll();
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(cargoClaseMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
