package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.ItemResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ItemDto;

@Component
public class ItemMapper {

    public ItemResumen toDomain(ItemDto dto) {
        if (dto == null) {
            return null;
        }
        return ItemResumen.builder()
                .legajoId(dto.getLegajoId())
                .codigoId(dto.getCodigoId())
                .anho(dto.getAnho())
                .mes(dto.getMes())
                .importe(dto.getImporte())
                .build();
    }
}
