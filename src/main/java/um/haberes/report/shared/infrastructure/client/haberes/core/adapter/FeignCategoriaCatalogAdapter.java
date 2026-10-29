package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CategoriaResumen;
import um.haberes.report.shared.domain.ports.out.CategoriaCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CategoriaClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CategoriaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CategoriaMapper;

import java.util.List;
import java.util.Objects;

@Component
@Cacheable("categorias")
@RequiredArgsConstructor
public class FeignCategoriaCatalogAdapter implements CategoriaCatalogPort {

    private final CategoriaClient categoriaClient;
    private final CategoriaMapper categoriaMapper;

    @Override
    public List<CategoriaResumen> findAll() {
        List<CategoriaDto> dtos = categoriaClient.findAll();
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(categoriaMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
