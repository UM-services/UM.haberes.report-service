package um.haberes.report.reports.docentessede.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.docentessede.domain.model.DocentesSedeContent;

import java.util.List;
import java.util.Objects;

@Component
public class DocentesSedeResponseMapper {

    public DocentesSedeResponse toResponse(DocentesSedeContent content) {
        if (content == null) {
            return new DocentesSedeResponse(null, null, null, null, List.of());
        }
        List<DocentesSedeResponse.Curso> cursos = content.cursos().stream()
                .filter(Objects::nonNull)
                .map(this::toCurso)
                .toList();
        return new DocentesSedeResponse(content.anho(), content.mes(), content.facultad(),
                content.geografica(), cursos);
    }

    private DocentesSedeResponse.Curso toCurso(DocentesSedeContent.Curso curso) {
        List<DocentesSedeResponse.Fila> filas = curso.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new DocentesSedeResponse.Fila(fila.cargo(), fila.docente(),
                        fila.designacion(), fila.horas(), fila.desarraigo()))
                .toList();
        return new DocentesSedeResponse.Curso(curso.nombre(), curso.periodo(), filas);
    }
}
