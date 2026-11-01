package um.haberes.report.reports.detallecodigo.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.detallecodigo.domain.model.DetalleCodigoContent;

import java.util.List;
import java.util.Objects;

@Component
public class DetalleCodigoResponseMapper {

    public DetalleCodigoResponse toResponse(DetalleCodigoContent content) {
        if (content == null) {
            return new DetalleCodigoResponse("", "", List.of());
        }
        List<DetalleCodigoResponse.Fila> filas = content.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new DetalleCodigoResponse.Fila(fila.legajoId(), fila.apellidoNombre(),
                        fila.liquidado(), fila.novedad(), text(fila.cantidad())))
                .toList();
        return new DetalleCodigoResponse(text(content.codigoId()), text(content.codigoNombre()),
                filas);
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
