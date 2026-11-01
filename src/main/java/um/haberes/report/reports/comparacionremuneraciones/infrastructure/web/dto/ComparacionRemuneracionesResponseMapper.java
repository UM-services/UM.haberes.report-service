package um.haberes.report.reports.comparacionremuneraciones.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.comparacionremuneraciones.domain.model.ComparacionRemuneracionesContent;

import java.util.List;
import java.util.Objects;

@Component
public class ComparacionRemuneracionesResponseMapper {

    public ComparacionRemuneracionesResponse toResponse(ComparacionRemuneracionesContent content) {
        if (content == null) {
            return new ComparacionRemuneracionesResponse(null, null, null, null, List.of());
        }
        List<ComparacionRemuneracionesResponse.Dependencia> dependencias = content.dependencias()
                .stream()
                .filter(Objects::nonNull)
                .map(this::toDependencia)
                .toList();
        return new ComparacionRemuneracionesResponse(content.anho(), content.mes(),
                content.anhoAnterior(), content.mesAnterior(), dependencias);
    }

    private ComparacionRemuneracionesResponse.Dependencia toDependencia(
            ComparacionRemuneracionesContent.Dependencia dependencia) {
        List<ComparacionRemuneracionesResponse.Fila> filas = dependencia.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new ComparacionRemuneracionesResponse.Fila(fila.legajoId(),
                        fila.apellidoNombre(), fila.brutoAnterior(), fila.brutoActual(),
                        fila.brutoDiferencia(), fila.brutoPorcentaje(), fila.netoAnterior(),
                        fila.netoActual(), fila.netoDiferencia()))
                .toList();
        return new ComparacionRemuneracionesResponse.Dependencia(dependencia.nombre(), filas);
    }
}
