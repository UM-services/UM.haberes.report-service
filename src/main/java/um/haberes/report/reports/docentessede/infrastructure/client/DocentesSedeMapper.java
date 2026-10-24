package um.haberes.report.reports.docentessede.infrastructure.client;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.docentessede.domain.model.CursoCargoContratado;
import um.haberes.report.reports.docentessede.domain.model.PersonaTesoreria;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ContratadoPersonaDto;
import um.haberes.report.shared.infrastructure.client.tesoreria.core.dto.CursoCargoContratadoDto;
import um.haberes.report.shared.infrastructure.client.tesoreria.core.dto.PersonaDto;

@Component
public class DocentesSedeMapper {

    public CursoCargoContratado toDomain(CursoCargoContratadoDto dto) {
        if (dto == null) {
            return null;
        }
        ContratadoPersonaDto persona = dto.getContratadoPersona();
        return new CursoCargoContratado(dto.getCursoId(), dto.getCargoTipoId(),
                persona == null ? null : persona.getPersonaId(),
                persona == null ? null : persona.getDocumentoId(),
                dto.getHorasSemanales());
    }

    public PersonaTesoreria toDomain(PersonaDto dto) {
        if (dto == null) {
            return null;
        }
        return new PersonaTesoreria(dto.getApellidoNombre());
    }
}
