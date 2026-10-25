package um.haberes.report.reports.basicosperiodo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.basicosperiodo.domain.model.BasicosPeriodoContent;
import um.haberes.report.reports.basicosperiodo.domain.model.BasicosPeriodoContent.Fila;
import um.haberes.report.reports.basicosperiodo.domain.ports.in.BuildBasicosPeriodoContentUseCase;
import um.haberes.report.shared.domain.model.CargoClaseDetalleResumen;
import um.haberes.report.shared.domain.model.CargoLiquidacionResumen;
import um.haberes.report.shared.domain.model.Importes;
import um.haberes.report.shared.domain.model.LiquidacionAdicionalResumen;
import um.haberes.report.shared.domain.model.LiquidacionResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.ports.out.CargoClaseDetalleCatalogPort;
import um.haberes.report.shared.domain.ports.out.CargoLiquidacionCatalogPort;
import um.haberes.report.shared.domain.ports.out.CodigoCatalogPort;
import um.haberes.report.shared.domain.ports.out.LiquidacionAdicionalCatalogPort;
import um.haberes.report.shared.domain.ports.out.LiquidacionCatalogPort;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Básicos por período (legacy: {@code SheetService.generateBasicos}).
 *
 * <p>Cinco consultas fijas al período (liquidaciones, cargos, cargos con clase, adicionales y
 * batch de personas) más el código 981. Antes era N+1 por legajo. El orden de filas y la
 * precedencia de columnas replican la planilla original.
 */
@Component
@RequiredArgsConstructor
public class BuildBasicosPeriodoContentUseCaseImpl implements BuildBasicosPeriodoContentUseCase {

    private static final int SIN_LIMITE = 0;
    private static final int CODIGO_ADICIONAL = 981;

    private final LiquidacionCatalogPort liquidacionCatalogPort;
    private final CargoLiquidacionCatalogPort cargoLiquidacionCatalogPort;
    private final CargoClaseDetalleCatalogPort cargoClaseDetalleCatalogPort;
    private final LiquidacionAdicionalCatalogPort liquidacionAdicionalCatalogPort;
    private final PersonaCatalogPort personaCatalogPort;
    private final CodigoCatalogPort codigoCatalogPort;

    @Override
    public BasicosPeriodoContent build(Integer anho, Integer mes) {
        List<LiquidacionResumen> liquidaciones = liquidacionCatalogPort
                .findAllByPeriodo(anho, mes, SIN_LIMITE).stream()
                .filter(Objects::nonNull)
                .filter(liquidacion -> liquidacion.getLegajoId() != null)
                .toList();
        if (liquidaciones.isEmpty()) {
            return new BasicosPeriodoContent(anho, mes, List.of());
        }

        Map<Long, List<CargoLiquidacionResumen>> cargos = cargoLiquidacionCatalogPort
                .findAllByPeriodo(anho, mes).stream()
                .filter(Objects::nonNull)
                .filter(cargo -> cargo.getLegajoId() != null)
                .collect(Collectors.groupingBy(CargoLiquidacionResumen::getLegajoId));
        Map<Long, List<CargoClaseDetalleResumen>> cargosClase = cargoClaseDetalleCatalogPort
                .findAllByPeriodo(anho, mes).stream()
                .filter(Objects::nonNull)
                .filter(cargo -> cargo.getLegajoId() != null)
                .collect(Collectors.groupingBy(CargoClaseDetalleResumen::getLegajoId));
        Map<Long, List<LiquidacionAdicionalResumen>> adicionales = liquidacionAdicionalCatalogPort
                .findAllByPeriodo(anho, mes).stream()
                .filter(Objects::nonNull)
                .filter(adicional -> adicional.getLegajoId() != null)
                .collect(Collectors.groupingBy(LiquidacionAdicionalResumen::getLegajoId));

        List<Long> legajoIds = liquidaciones.stream()
                .map(LiquidacionResumen::getLegajoId)
                .distinct()
                .toList();
        Map<Long, PersonaResumen> personas = personaCatalogPort.findAllByLegajos(legajoIds).stream()
                .filter(Objects::nonNull)
                .filter(persona -> persona.getLegajoId() != null)
                .collect(Collectors.toMap(PersonaResumen::getLegajoId, persona -> persona, (a, b) -> a));
        String codigoAdicional = codigoCatalogPort.findById(CODIGO_ADICIONAL)
                .map(codigo -> text(codigo.getNombre()))
                .orElse("");

        List<Fila> filas = new ArrayList<>();
        for (LiquidacionResumen liquidacion : liquidaciones) {
            Long legajoId = liquidacion.getLegajoId();
            PersonaResumen persona = personas.get(legajoId);
            String apellidoNombre = persona == null ? "" : persona.apellidoNombre();

            for (CargoLiquidacionResumen cargo : cargos.getOrDefault(legajoId, List.of())) {
                boolean porHora = cargo.getHorasJornada() != null
                        && cargo.getHorasJornada().compareTo(BigDecimal.ZERO) > 0;
                filas.add(new Fila(text(legajoId), apellidoNombre,
                        cargo.getDependencia() == null ? "" : text(cargo.getDependencia().getNombre()),
                        text(cargo.getCategoriaId()), text(cargo.getCategoriaNombre()),
                        Importes.format(cargo.getCategoriaBasico()),
                        porHora ? horas(cargo.getHorasJornada()) : "",
                        porHora ? "" : text(cargo.getJornada())));
            }
            for (CargoClaseDetalleResumen detalle : cargosClase.getOrDefault(legajoId, List.of())) {
                filas.add(new Fila(text(legajoId), apellidoNombre, text(detalle.getDependenciaNombre()),
                        text(detalle.getClaseNombre()),
                        text(detalle.getCargoClaseNombre()) + " / "
                                + text(detalle.getCargoClasePeriodoDescripcion()),
                        Importes.format(detalle.getValorHora()), "", text(detalle.getHoras())));
            }
            for (LiquidacionAdicionalResumen adicional : adicionales.getOrDefault(legajoId, List.of())) {
                filas.add(new Fila(text(legajoId), apellidoNombre, text(adicional.getDependenciaNombre()),
                        text(CODIGO_ADICIONAL), codigoAdicional,
                        Importes.format(adicional.getAdicional()), "", "1"));
            }
        }
        return new BasicosPeriodoContent(anho, mes, filas);
    }

    private String horas(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
