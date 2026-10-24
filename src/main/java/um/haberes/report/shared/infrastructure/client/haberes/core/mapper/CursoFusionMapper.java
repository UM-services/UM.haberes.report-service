package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CursoFusionResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoFusionDto;

@Component
public class CursoFusionMapper {

    public CursoFusionResumen toDomain(CursoFusionDto dto) {
        if (dto == null) {
            return null;
        }
        return CursoFusionResumen.builder()
                .cursoFusionId(dto.getCursoFusionId())
                .legajoId(dto.getLegajoId())
                .anho(dto.getAnho())
                .mes(dto.getMes())
                .facultadId(dto.getFacultadId())
                .geograficaId(dto.getGeograficaId())
                .cargoTipoId(dto.getCargoTipoId())
                .designacionTipoId(dto.getDesignacionTipoId())
                .categoriaId(dto.getCategoriaId())
                .anual(dto.getAnual())
                .build();
    }
}
