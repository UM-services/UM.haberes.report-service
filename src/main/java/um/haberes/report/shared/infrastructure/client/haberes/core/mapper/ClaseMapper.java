package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.ClaseResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ClaseDto;

@Component
public class ClaseMapper {

    public ClaseResumen toDomain(ClaseDto dto) {
        if (dto == null) {
            return null;
        }
        return ClaseResumen.builder()
                .claseId(dto.getClaseId())
                .nombre(dto.getNombre())
                .valorHora(dto.getValorHora())
                .build();
    }
}
