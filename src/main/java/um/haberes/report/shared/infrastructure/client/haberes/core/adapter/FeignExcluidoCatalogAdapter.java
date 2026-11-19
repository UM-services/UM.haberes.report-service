package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import um.haberes.report.shared.domain.model.ExcluidoResumen;
import um.haberes.report.shared.domain.ports.out.ExcluidoCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.ExcluidoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ExcluidoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.ExcluidoMapper;

@Component
@RequiredArgsConstructor
public class FeignExcluidoCatalogAdapter implements ExcluidoCatalogPort {

    private final ExcluidoClient excluidoClient;
    private final ExcluidoMapper excluidoMapper;

    @Override
    public List<ExcluidoResumen> findAllByPeriodo(Integer anho, Integer mes) {
        List<ExcluidoDto> dtos = excluidoClient.findAllByPeriodo(anho, mes);
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(excluidoMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
