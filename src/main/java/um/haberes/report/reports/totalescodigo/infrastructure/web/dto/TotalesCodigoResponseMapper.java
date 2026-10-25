package um.haberes.report.reports.totalescodigo.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.totalescodigo.domain.model.TotalesCodigoContent;

import java.util.List;
import java.util.Objects;

@Component
public class TotalesCodigoResponseMapper {

    public TotalesCodigoResponse toResponse(Integer anho, Integer mes, TotalesCodigoContent content) {
        List<TotalesCodigoResponse.Fila> filas = content == null ? List.of()
                : content.filas().stream()
                        .filter(Objects::nonNull)
                        .map(fila -> new TotalesCodigoResponse.Fila(fila.codigoId(), fila.nombre(),
                                fila.novedades(), fila.liquidado(), fila.diferencia()))
                        .toList();
        return new TotalesCodigoResponse(anho, mes, filas);
    }
}
