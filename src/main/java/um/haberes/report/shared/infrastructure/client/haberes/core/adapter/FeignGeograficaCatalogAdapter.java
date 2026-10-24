package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.GeograficaResumen;
import um.haberes.report.shared.domain.ports.out.GeograficaCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.GeograficaClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.GeograficaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.GeograficaMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FeignGeograficaCatalogAdapter implements GeograficaCatalogPort {

    private final GeograficaClient geograficaClient;
    private final GeograficaMapper geograficaMapper;

    @Override
    public Optional<GeograficaResumen> findById(Integer geograficaId) {
        return Optional.ofNullable(geograficaMapper.toDomain(geograficaClient.findByGeograficaId(geograficaId)));
    }

    @Override
    public List<GeograficaResumen> findAll() {
        return map(geograficaClient.findAll());
    }

    @Override
    public List<GeograficaResumen> findAllByIds(List<Integer> geograficaIds) {
        return map(geograficaClient.findAllByGeograficaIdIn(geograficaIds));
    }

    private List<GeograficaResumen> map(List<GeograficaDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(geograficaMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
