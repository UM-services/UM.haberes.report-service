package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CursoCargoResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoCargoDto;

@Component
@RequiredArgsConstructor
public class CursoCargoMapper {

    private final CursoMapper cursoMapper;
    private final CargoTipoMapper cargoTipoMapper;
    private final CategoriaMapper categoriaMapper;
    private final DesignacionTipoMapper designacionTipoMapper;
    private final PersonaMapper personaMapper;

    public CursoCargoResumen toDomain(CursoCargoDto dto) {
        if (dto == null) {
            return null;
        }
        return CursoCargoResumen.builder()
                .legajoId(dto.getLegajoId())
                .cursoId(dto.getCursoId())
                .cargoTipoId(dto.getCargoTipoId())
                .categoriaId(dto.getCategoriaId())
                .designacionTipoId(dto.getDesignacionTipoId())
                .horasSemanales(dto.getHorasSemanales())
                .desarraigo(dto.getDesarraigo())
                .curso(cursoMapper.toDomain(dto.getCurso()))
                .cargoTipo(cargoTipoMapper.toDomain(dto.getCargoTipo()))
                .categoria(categoriaMapper.toDomain(dto.getCategoria()))
                .designacionTipo(designacionTipoMapper.toDomain(dto.getDesignacionTipo()))
                .persona(personaMapper.toDomain(dto.getPersona()))
                .build();
    }
}
