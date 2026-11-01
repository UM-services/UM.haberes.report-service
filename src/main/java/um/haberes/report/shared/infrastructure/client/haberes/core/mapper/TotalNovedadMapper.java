package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.TotalNovedadResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.TotalNovedadDto;

@Component
public class TotalNovedadMapper {

    public TotalNovedadResumen toDomain(TotalNovedadDto dto) {
        if (dto == null) {
            return null;
        }
        return TotalNovedadResumen.builder()
                .codigoId(dto.getCodigoId())
                .total(dto.getTotal())
                .build();
    }
}
