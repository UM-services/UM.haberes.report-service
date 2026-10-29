package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.LiquidacionAdicionalResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LiquidacionAdicionalDto;

@Component
public class LiquidacionAdicionalMapper {

    public LiquidacionAdicionalResumen toDomain(LiquidacionAdicionalDto dto) {
        if (dto == null) {
            return null;
        }
        return LiquidacionAdicionalResumen.builder()
                .legajoId(dto.getLegajoId())
                .anho(dto.getAnho())
                .mes(dto.getMes())
                .dependenciaId(dto.getDependenciaId())
                .dependenciaNombre(dto.getDependencia() == null ? null : dto.getDependencia().getNombre())
                .adicional(dto.getAdicional())
                .build();
    }
}
