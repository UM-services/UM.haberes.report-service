package um.haberes.report.reports.cantidadcargos.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.cantidadcargos.domain.model.CantidadCargosContent;

import java.util.List;
import java.util.Objects;

@Component
public class CantidadCargosResponseMapper {

    public CantidadCargosResponse toResponse(CantidadCargosContent content) {
        if (content == null) {
            return new CantidadCargosResponse(null, null, List.of());
        }
        List<CantidadCargosResponse.Fila> filas = content.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new CantidadCargosResponse.Fila(fila.facultad(), fila.sede(),
                        fila.legajoId(), fila.apellidoNombre(), fila.anuales(), fila.semestre1(),
                        fila.semestre2(), fila.bruto()))
                .toList();
        return new CantidadCargosResponse(content.anho(), content.mes(), filas);
    }
}
