package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.FacultadResumen;
import um.haberes.report.shared.domain.ports.out.FacultadCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.FacultadClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.FacultadMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FeignFacultadCatalogAdapter implements FacultadCatalogPort {

    private final FacultadClient facultadClient;
    private final FacultadMapper facultadMapper;

    @Override
    public Optional<FacultadResumen> findById(Integer facultadId) {
        return Optional.ofNullable(facultadMapper.toDomain(facultadClient.findByFacultadId(facultadId)));
    }

    @Override
    public List<FacultadResumen> findAll() {
        return map(facultadClient.findAll());
    }

    @Override
    public List<FacultadResumen> findAllFacultades() {
        return map(facultadClient.findAllFacultades());
    }

    private List<FacultadResumen> map(List<FacultadDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(facultadMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
