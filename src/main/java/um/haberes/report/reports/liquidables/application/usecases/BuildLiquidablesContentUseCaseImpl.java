package um.haberes.report.reports.liquidables.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.liquidables.domain.model.LiquidablesContent;
import um.haberes.report.reports.liquidables.domain.model.LiquidablesContent.Fila;
import um.haberes.report.reports.liquidables.domain.ports.in.BuildLiquidablesContentUseCase;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;

import java.util.List;
import java.util.Objects;

/**
 * Planilla de liquidables: proyección directa de las personas con liquidación activa.
 * Una sola llamada a core (sin período ni N+1); el orden es el que devuelve el servicio.
 */
@Component
@RequiredArgsConstructor
public class BuildLiquidablesContentUseCaseImpl implements BuildLiquidablesContentUseCase {

    private final PersonaCatalogPort personaCatalogPort;

    @Override
    public LiquidablesContent build() {
        List<Fila> filas = personaCatalogPort.findAllLiquidables().stream()
                .filter(Objects::nonNull)
                .filter(persona -> persona.getLegajoId() != null)
                .map(persona -> new Fila(persona.getLegajoId(), persona.apellidoNombre(),
                        persona.getEstado(), persona.getDependenciaId(),
                        persona.getDependenciaNombre()))
                .toList();
        return new LiquidablesContent(filas);
    }
}
