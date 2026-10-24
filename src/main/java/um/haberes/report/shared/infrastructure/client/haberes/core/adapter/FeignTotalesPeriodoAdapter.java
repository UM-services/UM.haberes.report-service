package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.TotalItemResumen;
import um.haberes.report.shared.domain.model.TotalNovedadResumen;
import um.haberes.report.shared.domain.ports.out.TotalesPeriodoPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.TotalItemClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.TotalNovedadClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.TotalItemDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.TotalNovedadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.TotalItemMapper;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.TotalNovedadMapper;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignTotalesPeriodoAdapter implements TotalesPeriodoPort {

    private final TotalNovedadClient totalNovedadClient;
    private final TotalItemClient totalItemClient;
    private final TotalNovedadMapper totalNovedadMapper;
    private final TotalItemMapper totalItemMapper;

    @Override
    public List<TotalNovedadResumen> totalesNovedad(Integer anho, Integer mes) {
        List<TotalNovedadDto> dtos = totalNovedadClient.findAllByPeriodo(anho, mes);
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(totalNovedadMapper::toDomain).filter(Objects::nonNull).toList();
    }

    @Override
    public List<TotalItemResumen> totalesItem(Integer anho, Integer mes) {
        List<TotalItemDto> dtos = totalItemClient.findAllByPeriodo(anho, mes);
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(totalItemMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
