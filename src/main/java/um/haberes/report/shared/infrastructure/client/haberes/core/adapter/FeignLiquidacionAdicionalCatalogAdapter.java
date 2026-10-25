package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.LiquidacionAdicionalResumen;
import um.haberes.report.shared.domain.ports.out.LiquidacionAdicionalCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LiquidacionAdicionalClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionAdicionalDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.LiquidacionAdicionalMapper;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignLiquidacionAdicionalCatalogAdapter implements LiquidacionAdicionalCatalogPort {

    private final LiquidacionAdicionalClient liquidacionAdicionalClient;
    private final LiquidacionAdicionalMapper liquidacionAdicionalMapper;

    @Override
    public List<LiquidacionAdicionalResumen> findAllByPeriodo(Integer anho, Integer mes) {
        List<LiquidacionAdicionalDto> dtos = liquidacionAdicionalClient.findAllByPeriodo(anho, mes);
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(liquidacionAdicionalMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
