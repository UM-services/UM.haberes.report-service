package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.ContratadoPersonaResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ContratadoPersonaDto;

@Component
public class ContratadoPersonaMapper {

    public ContratadoPersonaResumen toDomain(ContratadoPersonaDto dto) {
        if (dto == null) {
            return null;
        }
        return ContratadoPersonaResumen.builder()
                .personaId(dto.getPersonaId())
                .documentoId(dto.getDocumentoId())
                .apellido(dto.getApellido())
                .nombre(dto.getNombre())
                .cuit(dto.getCuit())
                .build();
    }
}
