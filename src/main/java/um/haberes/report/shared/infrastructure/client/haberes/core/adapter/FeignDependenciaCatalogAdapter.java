package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.DependenciaResumen;
import um.haberes.report.shared.domain.ports.out.DependenciaCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.DependenciaClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DependenciaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.DependenciaMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Cacheable("dependencias")
@RequiredArgsConstructor
public class FeignDependenciaCatalogAdapter implements DependenciaCatalogPort {

    private final DependenciaClient dependenciaClient;
    private final DependenciaMapper dependenciaMapper;

    @Override
    public Optional<DependenciaResumen> findById(Integer dependenciaId) {
        return Optional.ofNullable(dependenciaMapper.toDomain(dependenciaClient.findByDependenciaId(dependenciaId)));
    }

    @Override
    public List<DependenciaResumen> findAll() {
        List<DependenciaDto> dtos = dependenciaClient.findAll();
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(dependenciaMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
