package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.LiquidacionResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionDto;

@Component
public class LiquidacionMapper {

    public LiquidacionResumen toDomain(LiquidacionDto dto) {
        if (dto == null) {
            return null;
        }
        return LiquidacionResumen.builder()
                .legajoId(dto.getLegajoId())
                .anho(dto.getAnho())
                .mes(dto.getMes())
                .dependenciaId(dto.getDependenciaId())
                .totalRemunerativo(dto.getTotalRemunerativo())
                .totalNoRemunerativo(dto.getTotalNoRemunerativo())
                .totalDeduccion(dto.getTotalDeduccion())
                .totalNeto(dto.getTotalNeto())
                .build();
    }
}
