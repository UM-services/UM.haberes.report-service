package um.haberes.report.reports.cargosclaseperiodo.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoDefinicion;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.out.CargoClaseCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoClaseClient;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FeignCargoClasePeriodoDefinicionAdapter implements CargoClaseCatalogPort {

    private final CargoClaseClient cargoClaseClient;
    private final CargosClasePeriodoMapper mapper;

    @Override
    public Optional<CargoClasePeriodoDefinicion> findByCargoClaseId(Long cargoClaseId) {
        return Optional.ofNullable(
                mapper.toDefinicion(cargoClaseClient.findByCargoClaseId(cargoClaseId)));
    }
}
