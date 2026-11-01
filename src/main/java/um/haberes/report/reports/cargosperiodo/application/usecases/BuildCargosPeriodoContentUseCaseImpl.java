package um.haberes.report.reports.cargosperiodo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cargosperiodo.domain.model.CargosPeriodoContent;
import um.haberes.report.reports.cargosperiodo.domain.model.CargosPeriodoContent.Fila;
import um.haberes.report.reports.cargosperiodo.domain.ports.in.BuildCargosPeriodoContentUseCase;
import um.haberes.report.shared.domain.model.CargoLiquidacionResumen;
import um.haberes.report.shared.domain.model.DependenciaResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.ports.out.CargoLiquidacionCatalogPort;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Cargos por período (legacy: {@code SheetService.generateCargos}).
 *
 * <p>Dos consultas fijas: los cargos activos del período y el batch de personas. Diferencia
 * deliberada con la planilla original: <b>no</b> se borran los cargos que no están en las
 * liquidaciones (un servicio de reportes no muta datos); la ruta legacy de core sigue igual
 * para el cliente de escritorio.
 */
@Component
@RequiredArgsConstructor
public class BuildCargosPeriodoContentUseCaseImpl implements BuildCargosPeriodoContentUseCase {

    private final CargoLiquidacionCatalogPort cargoLiquidacionCatalogPort;
    private final PersonaCatalogPort personaCatalogPort;

    @Override
    public CargosPeriodoContent build(Integer anho, Integer mes) {
        List<CargoLiquidacionResumen> cargos = cargoLiquidacionCatalogPort
                .findAllActivosByPeriodo(anho, mes).stream()
                .filter(Objects::nonNull)
                .filter(cargo -> cargo.getLegajoId() != null)
                .toList();
        if (cargos.isEmpty()) {
            return new CargosPeriodoContent(anho, mes, List.of());
        }

        List<Long> legajoIds = cargos.stream()
                .map(CargoLiquidacionResumen::getLegajoId)
                .distinct()
                .toList();
        Map<Long, PersonaResumen> personas = personaCatalogPort.findAllByLegajos(legajoIds).stream()
                .filter(Objects::nonNull)
                .filter(persona -> persona.getLegajoId() != null)
                .collect(Collectors.toMap(PersonaResumen::getLegajoId, persona -> persona, (a, b) -> a));

        List<Fila> filas = new ArrayList<>();
        for (CargoLiquidacionResumen cargo : cargos) {
            PersonaResumen persona = personas.get(cargo.getLegajoId());
            DependenciaResumen dependencia = cargo.getDependencia();
            filas.add(new Fila(text(cargo.getLegajoId()),
                    persona == null ? "" : persona.apellidoNombre(),
                    mes + "/" + anho,
                    text(cargo.getDependenciaId()),
                    dependencia == null ? "" : text(dependencia.getNombre()),
                    text(cargo.getCategoriaId()),
                    text(cargo.getCategoriaNombre())));
        }
        return new CargosPeriodoContent(anho, mes, filas);
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
