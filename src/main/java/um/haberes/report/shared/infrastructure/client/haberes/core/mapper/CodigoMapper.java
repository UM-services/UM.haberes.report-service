package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CodigoResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoDto;

@Component
public class CodigoMapper {

    public CodigoResumen toDomain(CodigoDto dto) {
        if (dto == null) {
            return null;
        }
        return CodigoResumen.builder()
                .codigoId(dto.getCodigoId())
                .nombre(dto.getNombre())
                .build();
    }
}
