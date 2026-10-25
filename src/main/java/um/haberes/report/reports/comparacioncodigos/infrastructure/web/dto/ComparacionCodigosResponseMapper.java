package um.haberes.report.reports.comparacioncodigos.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.comparacioncodigos.domain.model.ComparacionCodigosContent;

import java.util.List;
import java.util.Objects;

@Component
public class ComparacionCodigosResponseMapper {

    public ComparacionCodigosResponse toResponse(Integer anho, Integer mes,
                                                 ComparacionCodigosContent content) {
        List<ComparacionCodigosResponse.Fila> filas = content == null ? List.of()
                : content.filas().stream()
                        .filter(Objects::nonNull)
                        .map(fila -> new ComparacionCodigosResponse.Fila(fila.codigoId(), fila.nombre(),
                                fila.novedades(), fila.liquidado(), fila.diferencia()))
                        .toList();
        return new ComparacionCodigosResponse(anho, mes, filas);
    }
}
