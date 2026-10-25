package um.haberes.report.reports.designacionesdocente.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.designacionesdocente.domain.model.DesignacionesDocenteContent;

import java.util.List;
import java.util.Objects;

@Component
public class DesignacionesDocenteResponseMapper {

    public DesignacionesDocenteResponse toResponse(DesignacionesDocenteContent content) {
        if (content == null) {
            return new DesignacionesDocenteResponse(null, null, List.of());
        }
        List<DesignacionesDocenteResponse.Docente> docentes = content.docentes().stream()
                .filter(Objects::nonNull)
                .map(this::toDocente)
                .toList();
        return new DesignacionesDocenteResponse(content.anho(), content.mes(), docentes);
    }

    private DesignacionesDocenteResponse.Docente toDocente(
            DesignacionesDocenteContent.Docente docente) {
        List<DesignacionesDocenteResponse.Fila> filas = docente.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new DesignacionesDocenteResponse.Fila(fila.facultad(), fila.sede(),
                        fila.espacio(), fila.horas(), fila.cargo(), fila.designacion(),
                        fila.horasDesignacion(), fila.dictado()))
                .toList();
        return new DesignacionesDocenteResponse.Docente(docente.legajoId(),
                docente.apellidoNombre(), filas);
    }
}
