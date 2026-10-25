package um.haberes.report.reports.librosueldos.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.librosueldos.domain.model.LibroSueldosContent;

import java.util.List;
import java.util.Objects;

@Component
public class LibroSueldosResponseMapper {

    public LibroSueldosResponse toResponse(LibroSueldosContent content) {
        if (content == null) {
            return new LibroSueldosResponse(null, null, List.of());
        }
        List<LibroSueldosResponse.Fila> filas = content.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new LibroSueldosResponse.Fila(fila.legajoId(), fila.apellidoNombre(),
                        fila.cuil(), fila.dependencia(), fila.remunerativo(), fila.noRemunerativo(),
                        fila.deducciones(), fila.neto()))
                .toList();
        return new LibroSueldosResponse(content.anho(), content.mes(), filas);
    }
}
