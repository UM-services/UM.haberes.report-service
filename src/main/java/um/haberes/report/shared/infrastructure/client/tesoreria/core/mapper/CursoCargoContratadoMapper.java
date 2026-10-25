package um.haberes.report.shared.infrastructure.client.tesoreria.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CursoCargoContratadoResumen;
import um.haberes.report.shared.infrastructure.client.tesoreria.core.dto.CursoCargoContratadoDto;

@Component
public class CursoCargoContratadoMapper {

    public CursoCargoContratadoResumen toDomain(CursoCargoContratadoDto dto) {
        if (dto == null) {
            return null;
        }
        return CursoCargoContratadoResumen.builder()
                .cursoCargoContratadoId(dto.getCursoCargoContratadoId())
                .cursoId(dto.getCursoId())
                .anho(dto.getAnho())
                .mes(dto.getMes())
                .contratoId(dto.getContratoId())
                .personaId(dto.getPersonaId())
                .documentoId(dto.getDocumentoId())
                .cargoTipoId(dto.getCargoTipoId())
                .horasSemanales(dto.getHorasSemanales())
                .build();
    }
}
