package um.haberes.report.reports.cursoscontratado.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.cursoscontratado.domain.model.CursosContratadoContent;

import java.util.List;
import java.util.Objects;

@Component
public class CursosContratadoResponseMapper {

    public CursosContratadoResponse toResponse(CursosContratadoContent content) {
        if (content == null) {
            return new CursosContratadoResponse(null, null, List.of());
        }
        List<CursosContratadoResponse.Fila> filas = content.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new CursosContratadoResponse.Fila(fila.cuit(), fila.apellidoNombre(),
                        fila.periodo(), fila.facultad(), fila.sede(), fila.curso(), fila.cargo(),
                        fila.horas(), fila.dictado()))
                .toList();
        return new CursosContratadoResponse(content.anho(), content.mes(), filas);
    }
}
