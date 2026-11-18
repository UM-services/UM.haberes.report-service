package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;

import um.haberes.report.shared.domain.model.ExcluidoResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ExcluidoDto;

@Component
public class ExcluidoMapper {

    public ExcluidoResumen toDomain(ExcluidoDto dto) {
        if (dto == null) {
            return null;
        }
        return ExcluidoResumen.builder()
                .legajoId(dto.getLegajoId())
                .anho(dto.getAnho())
                .mes(dto.getMes())
                .fecha(dto.getFecha())
                .observaciones(dto.getObservaciones())
                .build();
    }
}
