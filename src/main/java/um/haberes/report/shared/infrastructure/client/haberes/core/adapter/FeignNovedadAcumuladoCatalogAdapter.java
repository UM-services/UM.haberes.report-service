package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.NovedadAcumuladoResumen;
import um.haberes.report.shared.domain.ports.out.NovedadAcumuladoCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.NovedadAcumuladoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.NovedadAcumuladoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.NovedadAcumuladoMapper;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignNovedadAcumuladoCatalogAdapter implements NovedadAcumuladoCatalogPort {

    private final NovedadAcumuladoClient novedadAcumuladoClient;
    private final NovedadAcumuladoMapper novedadAcumuladoMapper;

    @Override
    public List<NovedadAcumuladoResumen> findAllByCodigo(Integer codigoId, Integer anho, Integer mes) {
        List<NovedadAcumuladoDto> dtos = novedadAcumuladoClient.findAllByCodigo(codigoId, anho, mes);
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(novedadAcumuladoMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
