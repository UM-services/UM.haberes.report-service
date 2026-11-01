package um.haberes.report.reports.totalesgenerales.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalesGeneralesContent;

import java.util.List;
import java.util.Objects;

@Component
public class TotalesGeneralesResponseMapper {

    public TotalesGeneralesResponse toResponse(TotalesGeneralesContent content) {
        if (content == null) {
            return new TotalesGeneralesResponse(null, null, List.of());
        }
        return new TotalesGeneralesResponse(content.anho(), content.mes(),
                content.grupos().stream()
                        .filter(Objects::nonNull)
                        .map(this::toGrupo)
                        .toList());
    }

    private TotalesGeneralesResponse.Grupo toGrupo(TotalesGeneralesContent.Grupo grupo) {
        List<TotalesGeneralesResponse.Fila> filas = grupo.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new TotalesGeneralesResponse.Fila(
                        fila.codigoId(), fila.nombre(), fila.total()))
                .toList();
        return new TotalesGeneralesResponse.Grupo(grupo.nombre(), filas, grupo.subtotal());
    }
}
