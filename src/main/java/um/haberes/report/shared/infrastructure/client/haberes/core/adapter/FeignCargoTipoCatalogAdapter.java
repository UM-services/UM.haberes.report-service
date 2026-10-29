package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CargoTipoResumen;
import um.haberes.report.shared.domain.ports.out.CargoTipoCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoTipoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoTipoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CargoTipoMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Cacheable("cargoTipos")
@RequiredArgsConstructor
public class FeignCargoTipoCatalogAdapter implements CargoTipoCatalogPort {

    private final CargoTipoClient cargoTipoClient;
    private final CargoTipoMapper cargoTipoMapper;

    @Override
    public Optional<CargoTipoResumen> findById(Integer cargoTipoId) {
        return Optional.ofNullable(cargoTipoMapper.toDomain(cargoTipoClient.findByCargoTipoId(cargoTipoId)));
    }

    @Override
    public List<CargoTipoResumen> findAll() {
        List<CargoTipoDto> dtos = cargoTipoClient.findAll();
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(cargoTipoMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
