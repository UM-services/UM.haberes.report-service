package um.haberes.report.reports.comparacionremuneraciones.infrastructure.client;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.comparacionremuneraciones.domain.model.LiquidacionRemuneracion;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionDto;

@Component
public class ComparacionRemuneracionesMapper {

    public LiquidacionRemuneracion toDomain(LiquidacionDto dto) {
        if (dto == null) {
            return null;
        }
        return new LiquidacionRemuneracion(dto.getLegajoId(), dto.getTotalRemunerativo(),
                dto.getTotalNeto());
    }
}
