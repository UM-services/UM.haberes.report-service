package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.ItemResumen;
import um.haberes.report.shared.domain.ports.out.ItemCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.ItemClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ItemDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.ItemMapper;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignItemCatalogAdapter implements ItemCatalogPort {

    private final ItemClient itemClient;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemResumen> findAllByCodigo(Integer codigoId, Integer anho, Integer mes) {
        List<ItemDto> dtos = itemClient.findAllByCodigoId(codigoId, anho, mes);
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(itemMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
