package um.haberes.report.reports.cargosclaseperiodo.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargosClasePeriodoContent;

import java.util.List;
import java.util.Objects;

@Component
public class CargosClasePeriodoResponseMapper {

    public CargosClasePeriodoResponse toResponse(CargosClasePeriodoContent content) {
        if (content == null) {
            return new CargosClasePeriodoResponse(null, null, null, List.of());
        }
        List<CargosClasePeriodoResponse.Sede> sedes = content.sedes().stream()
                .filter(Objects::nonNull)
                .map(this::toSede)
                .toList();
        return new CargosClasePeriodoResponse(content.anho(), content.mes(), content.facultad(),
                sedes);
    }

    private CargosClasePeriodoResponse.Sede toSede(CargosClasePeriodoContent.Sede sede) {
        List<CargosClasePeriodoResponse.Fila> filas = sede.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new CargosClasePeriodoResponse.Fila(fila.legajoId(),
                        fila.apellidoNombre(), fila.cargo(), fila.descripcion(), fila.periodoDesde(),
                        fila.periodoHasta(), fila.horas(), fila.clase()))
                .toList();
        return new CargosClasePeriodoResponse.Sede(sede.nombre(), filas);
    }
}
