package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;

@Component
public class PersonaMapper {

    public PersonaResumen toDomain(PersonaDto dto) {
        if (dto == null) {
            return null;
        }
        return PersonaResumen.builder()
                .legajoId(dto.getLegajoId())
                .documento(dto.getDocumento())
                .apellido(dto.getApellido())
                .nombre(dto.getNombre())
                .cuil(dto.getCuil())
                .dependenciaId(dto.getDependenciaId())
                .directivoEtec(dto.getDirectivoEtec())
                .altaAdministrativa(dto.getAltaAdministrativa())
                .build();
    }
}
