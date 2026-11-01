package um.haberes.report.reports.cursodocente.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.cursodocente.domain.model.CursoDocenteContent;

import java.util.List;
import java.util.Objects;

@Component
public class CursoDocenteResponseMapper {

    public CursoDocenteResponse toResponse(CursoDocenteContent content) {
        if (content == null) {
            return new CursoDocenteResponse(null, null, List.of());
        }
        List<CursoDocenteResponse.Docente> docentes = content.docentes().stream()
                .filter(Objects::nonNull)
                .map(this::toDocente)
                .toList();
        return new CursoDocenteResponse(content.anho(), content.mes(), docentes);
    }

    private CursoDocenteResponse.Docente toDocente(CursoDocenteContent.Docente docente) {
        List<CursoDocenteResponse.Fila> filas = docente.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new CursoDocenteResponse.Fila(fila.facultad(), fila.sede(),
                        fila.espacio(), fila.horas(), fila.cargo(), fila.basico(), fila.anual(),
                        fila.semestre1(), fila.semestre2()))
                .toList();
        return new CursoDocenteResponse.Docente(docente.legajoId(), docente.apellidoNombre(), filas);
    }
}
