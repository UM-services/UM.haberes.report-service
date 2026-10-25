package um.haberes.report.reports.fusiondocente.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.fusiondocente.domain.model.FusionDocenteContent;

import java.util.List;
import java.util.Objects;

@Component
public class FusionDocenteResponseMapper {

    public FusionDocenteResponse toResponse(FusionDocenteContent content) {
        if (content == null) {
            return new FusionDocenteResponse(null, null, List.of());
        }
        List<FusionDocenteResponse.Fila> filas = content.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new FusionDocenteResponse.Fila(fila.legajoId(), fila.apellidoNombre(),
                        fila.facultad(), fila.sede(), fila.cargo(), fila.basico()))
                .toList();
        return new FusionDocenteResponse(content.anho(), content.mes(), filas);
    }
}
