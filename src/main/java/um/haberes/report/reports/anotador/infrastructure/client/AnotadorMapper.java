package um.haberes.report.reports.anotador.infrastructure.client;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.anotador.domain.model.AnotadorRegistro;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.AnotadorDto;

@Component
public class AnotadorMapper {

    public AnotadorRegistro toDomain(AnotadorDto dto) {
        if (dto == null) {
            return null;
        }
        return new AnotadorRegistro(dto.getLegajoId(),
                dto.getPersona() == null ? null : text(dto.getPersona().getDocumento()),
                dto.getPersona() == null ? null : dto.getPersona().getApellidoNombre(),
                dto.getFacultad() == null ? null : dto.getFacultad().getNombre(),
                dto.getCreated(), dto.getUser(), dto.getAutorizado(), dto.getRechazado(),
                dto.getAnotacion(), dto.getRespuesta());
    }

    private String text(Object value) {
        return value == null ? null : value.toString();
    }
}
