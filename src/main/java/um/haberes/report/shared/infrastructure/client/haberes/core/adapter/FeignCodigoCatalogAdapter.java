package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CodigoResumen;
import um.haberes.report.shared.domain.ports.out.CodigoCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CodigoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CodigoMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Cacheable("codigos")
@RequiredArgsConstructor
public class FeignCodigoCatalogAdapter implements CodigoCatalogPort {

    private final CodigoClient codigoClient;
    private final CodigoMapper codigoMapper;

    @Override
    public Optional<CodigoResumen> findById(Integer codigoId) {
        return Optional.ofNullable(codigoMapper.toDomain(codigoClient.findByCodigoId(codigoId)));
    }

    @Override
    public List<CodigoResumen> findAllByPeriodo(Integer anho, Integer mes) {
        List<CodigoDto> dtos = codigoClient.findAllByPeriodo(anho, mes);
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(codigoMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
