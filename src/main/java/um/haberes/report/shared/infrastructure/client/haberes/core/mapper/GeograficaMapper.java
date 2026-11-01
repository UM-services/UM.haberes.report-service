package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.GeograficaResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.GeograficaDto;

@Component
public class GeograficaMapper {

    public GeograficaResumen toDomain(GeograficaDto dto) {
        if (dto == null) {
            return null;
        }
        return GeograficaResumen.builder()
                .geograficaId(dto.getGeograficaId())
                .nombre(dto.getNombre())
                .build();
    }
}
