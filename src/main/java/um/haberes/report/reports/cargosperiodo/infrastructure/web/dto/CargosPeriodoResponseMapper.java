package um.haberes.report.reports.cargosperiodo.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.cargosperiodo.domain.model.CargosPeriodoContent;

import java.util.List;
import java.util.Objects;

@Component
public class CargosPeriodoResponseMapper {

    public CargosPeriodoResponse toResponse(CargosPeriodoContent content) {
        if (content == null) {
            return new CargosPeriodoResponse(null, null, List.of());
        }
        List<CargosPeriodoResponse.Fila> filas = content.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new CargosPeriodoResponse.Fila(fila.legajoId(), fila.apellidoNombre(),
                        fila.periodo(), fila.dependenciaId(), fila.dependenciaNombre(),
                        fila.categoriaId(), fila.categoriaNombre()))
                .toList();
        return new CargosPeriodoResponse(content.anho(), content.mes(), filas);
    }
}
