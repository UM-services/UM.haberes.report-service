package um.haberes.report.reports.basicosperiodo.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.basicosperiodo.domain.model.BasicosPeriodoContent;

import java.util.List;
import java.util.Objects;

@Component
public class BasicosPeriodoResponseMapper {

    public BasicosPeriodoResponse toResponse(BasicosPeriodoContent content) {
        if (content == null) {
            return new BasicosPeriodoResponse(null, null, List.of());
        }
        List<BasicosPeriodoResponse.Fila> filas = content.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new BasicosPeriodoResponse.Fila(fila.legajoId(), fila.apellidoNombre(),
                        fila.dependencia(), fila.claseCodigo(), fila.categoriaCargo(), fila.basico(),
                        fila.horasJornada(), fila.jornada()))
                .toList();
        return new BasicosPeriodoResponse(content.anho(), content.mes(), filas);
    }
}
