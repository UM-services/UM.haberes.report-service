package um.haberes.report.reports.comparacionremuneraciones.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.comparacionremuneraciones.domain.model.LiquidacionRemuneracion;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.out.LiquidacionAcreditadoCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.LiquidacionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionDto;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignLiquidacionAcreditadoCatalogAdapter implements LiquidacionAcreditadoCatalogPort {

    private final LiquidacionClient liquidacionClient;
    private final ComparacionRemuneracionesMapper mapper;

    @Override
    public List<LiquidacionRemuneracion> findAllByAcreditado(Integer anho, Integer mes) {
        List<LiquidacionDto> dtos = liquidacionClient.findAllByAcreditado(anho, mes);
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(mapper::toDomain).filter(Objects::nonNull).toList();
    }
}
