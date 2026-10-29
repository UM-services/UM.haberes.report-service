package um.haberes.report.reports.sheet.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.sheet.domain.model.SheetCursosContent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Component
public class SheetCursosResponseMapper {

    public SheetCursosResponse toResponse(SheetCursosContent content) {
        if (content == null) {
            return new SheetCursosResponse(null, null, List.of());
        }
        List<SheetCursosResponse.Fila> filas = content.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new SheetCursosResponse.Fila(text(fila.legajoId()), text(fila.apellidoNombre()),
                        text(fila.periodo()), text(fila.facultad()), text(fila.geografica()),
                        text(fila.curso()), text(fila.cargo()), horas(fila.horas()),
                        text(fila.dictado())))
                .toList();
        return new SheetCursosResponse(content.anho(), content.mes(), filas);
    }

    private String horas(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
