package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CategoriaResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CategoriaDto;

@Component
public class CategoriaMapper {

    public CategoriaResumen toDomain(CategoriaDto dto) {
        if (dto == null) {
            return null;
        }
        return CategoriaResumen.builder()
                .categoriaId(dto.getCategoriaId())
                .nombre(dto.getNombre())
                .basico(dto.getBasico())
                .build();
    }
}
