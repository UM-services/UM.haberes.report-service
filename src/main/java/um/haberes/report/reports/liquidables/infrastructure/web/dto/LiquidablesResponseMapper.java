package um.haberes.report.reports.liquidables.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.liquidables.domain.model.LiquidablesContent;

import java.util.List;
import java.util.Objects;

@Component
public class LiquidablesResponseMapper {

    public LiquidablesResponse toResponse(LiquidablesContent content) {
        if (content == null) {
            return new LiquidablesResponse(List.of());
        }
        List<LiquidablesResponse.Fila> filas = content.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new LiquidablesResponse.Fila(text(fila.legajoId()),
                        text(fila.apellidoNombre()), text(fila.estado()), text(fila.dependenciaId()),
                        text(fila.dependenciaNombre())))
                .toList();
        return new LiquidablesResponse(filas);
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
