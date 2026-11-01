package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.LegajoCursoCantidadResumen;
import um.haberes.report.shared.domain.ports.out.LegajoCursoCantidadCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LegajoCursoCantidadClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LegajoCursoCantidadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.LegajoCursoCantidadMapper;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignLegajoCursoCantidadCatalogAdapter implements LegajoCursoCantidadCatalogPort {

    private final LegajoCursoCantidadClient legajoCursoCantidadClient;
    private final LegajoCursoCantidadMapper legajoCursoCantidadMapper;

    @Override
    public List<LegajoCursoCantidadResumen> findAllByPeriodo(Integer anho, Integer mes) {
        List<LegajoCursoCantidadDto> dtos = legajoCursoCantidadClient.findAllByPeriodo(anho, mes);
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream()
                .map(legajoCursoCantidadMapper::toDomain)
                .filter(Objects::nonNull)
                .toList();
    }
}
