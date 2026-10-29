package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.ClaseResumen;
import um.haberes.report.shared.domain.ports.out.ClaseCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.ClaseClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ClaseDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.ClaseMapper;

import java.util.List;
import java.util.Objects;

@Component
@Cacheable("clases")
@RequiredArgsConstructor
public class FeignClaseCatalogAdapter implements ClaseCatalogPort {

    private final ClaseClient claseClient;
    private final ClaseMapper claseMapper;

    @Override
    public List<ClaseResumen> findAll() {
        List<ClaseDto> dtos = claseClient.findAll();
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(claseMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
