package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.TotalItemResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.TotalItemDto;

@Component
public class TotalItemMapper {

    public TotalItemResumen toDomain(TotalItemDto dto) {
        if (dto == null) {
            return null;
        }
        return TotalItemResumen.builder()
                .codigoId(dto.getCodigoId())
                .total(dto.getTotal())
                .build();
    }
}
