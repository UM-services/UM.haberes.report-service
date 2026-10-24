package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CursoFusionResumen;
import um.haberes.report.shared.domain.ports.out.CursoFusionCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CursoFusionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoFusionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CursoFusionMapper;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignCursoFusionCatalogAdapter implements CursoFusionCatalogPort {

    private final CursoFusionClient cursoFusionClient;
    private final CursoFusionMapper cursoFusionMapper;

    @Override
    public List<CursoFusionResumen> findAllByLegajoId(Long legajoId, Integer anho, Integer mes) {
        List<CursoFusionDto> dtos = cursoFusionClient.findAllByLegajoId(legajoId, anho, mes);
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(cursoFusionMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
