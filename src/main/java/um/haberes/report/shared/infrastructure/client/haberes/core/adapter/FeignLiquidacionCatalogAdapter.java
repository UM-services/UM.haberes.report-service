package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.LiquidacionResumen;
import um.haberes.report.shared.domain.ports.out.LiquidacionCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LiquidacionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.LiquidacionMapper;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignLiquidacionCatalogAdapter implements LiquidacionCatalogPort {

    private final LiquidacionClient liquidacionClient;
    private final LiquidacionMapper liquidacionMapper;

    @Override
    public List<LiquidacionResumen> findAllByPeriodo(Integer anho, Integer mes, Integer limit) {
        return map(liquidacionClient.findAllByPeriodo(anho, mes, limit));
    }

    @Override
    public List<LiquidacionResumen> findAllByAcreditado(Integer anho, Integer mes) {
        return map(liquidacionClient.findAllByAcreditado(anho, mes));
    }

    @Override
    public List<LiquidacionResumen> findAllByDependencia(
            Integer dependenciaId, Integer anho, Integer mes, String salida) {
        return map(liquidacionClient.findAllByDependencia(dependenciaId, anho, mes, salida));
    }

    private List<LiquidacionResumen> map(List<LiquidacionDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(liquidacionMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
