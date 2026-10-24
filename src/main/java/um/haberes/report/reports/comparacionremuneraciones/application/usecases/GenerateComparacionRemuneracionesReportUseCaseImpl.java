package um.haberes.report.reports.comparacionremuneraciones.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.comparacionremuneraciones.domain.model.ComparacionRemuneracionesContent;
import um.haberes.report.reports.comparacionremuneraciones.domain.model.ComparacionRemuneracionesContent.Dependencia;
import um.haberes.report.reports.comparacionremuneraciones.domain.model.ComparacionRemuneracionesContent.Fila;
import um.haberes.report.reports.comparacionremuneraciones.domain.model.LiquidacionRemuneracion;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.in.GenerateComparacionRemuneracionesReportUseCase;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.out.ComparacionRemuneracionesPdfRenderer;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.out.LiquidacionAcreditadoCatalogPort;
import um.haberes.report.shared.domain.model.DependenciaResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.DependenciaCatalogPort;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateComparacionRemuneracionesReportUseCaseImpl
        implements GenerateComparacionRemuneracionesReportUseCase {

    static final String DISPLAY_NAME = "comparacion-remuneraciones.pdf";
    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(new Locale("en", "ES"));

    private final LiquidacionAcreditadoCatalogPort liquidacionPort;
    private final PersonaCatalogPort personaCatalogPort;
    private final DependenciaCatalogPort dependenciaCatalogPort;
    private final ComparacionRemuneracionesPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes, Integer anhoAnterior, Integer mesAnterior) {
        List<LiquidacionRemuneracion> actuales = liquidacionPort.findAllByAcreditado(anho, mes);
        List<LiquidacionRemuneracion> anteriores =
                liquidacionPort.findAllByAcreditado(anhoAnterior, mesAnterior);
        List<PersonaResumen> personas = personaCatalogPort.findAll();
        List<DependenciaResumen> dependencias = dependenciaCatalogPort.findAll();

        Map<Long, LiquidacionRemuneracion> actualesMap = byLegajo(actuales);
        Map<Long, LiquidacionRemuneracion> anterioresMap = byLegajo(anteriores);
        Map<Integer, DependenciaResumen> dependenciasMap = dependencias.stream()
                .filter(Objects::nonNull)
                .filter(dependencia -> dependencia.getDependenciaId() != null)
                .collect(Collectors.toMap(DependenciaResumen::getDependenciaId, d -> d, (a, b) -> a));
        Map<Integer, List<PersonaResumen>> personasPorDependencia = personas.stream()
                .filter(Objects::nonNull)
                .filter(persona -> persona.getDependenciaId() != null)
                .collect(Collectors.groupingBy(PersonaResumen::getDependenciaId));

        List<Dependencia> content = new ArrayList<>();
        for (Integer dependenciaId : dependenciasMap.keySet().stream().sorted().toList()) {
            List<PersonaResumen> personasDeDependencia = personasPorDependencia.get(dependenciaId);
            if (personasDeDependencia == null || personasDeDependencia.isEmpty()) {
                continue;
            }
            DependenciaResumen dependencia = dependenciasMap.get(dependenciaId);
            String nombre = dependencia == null || dependencia.getNombre() == null
                    ? "Sin nombre" : dependencia.getNombre();

            List<Fila> filas = personasDeDependencia.stream()
                    .sorted(Comparator
                            .comparing(PersonaResumen::getApellido, Comparator.nullsLast(String::compareToIgnoreCase))
                            .thenComparing(PersonaResumen::getNombre, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .map(persona -> buildFila(persona, actualesMap, anterioresMap))
                    .filter(Objects::nonNull)
                    .toList();
            content.add(new Dependencia(nombre, filas));
        }

        Path target = storage.resolve(fileName(anho, mes, anhoAnterior, mesAnterior));
        renderer.render(new ComparacionRemuneracionesContent(anho, mes, anhoAnterior, mesAnterior, content),
                target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private Fila buildFila(PersonaResumen persona,
                           Map<Long, LiquidacionRemuneracion> actuales,
                           Map<Long, LiquidacionRemuneracion> anteriores) {
        LiquidacionRemuneracion actual = persona.getLegajoId() == null
                ? null : actuales.get(persona.getLegajoId());
        if (actual == null) {
            log.warn("No se encontró liquidación actual para legajo ID: {}", persona.getLegajoId());
            return null;
        }
        LiquidacionRemuneracion anterior = anteriores.getOrDefault(persona.getLegajoId(),
                new LiquidacionRemuneracion(persona.getLegajoId(), BigDecimal.ZERO, BigDecimal.ZERO));

        BigDecimal brutoActual = value(actual.totalRemunerativo());
        BigDecimal brutoAnterior = value(anterior.totalRemunerativo());
        BigDecimal netoActual = value(actual.totalNeto());
        BigDecimal netoAnterior = value(anterior.totalNeto());

        BigDecimal diferenciaBruto = brutoActual.subtract(brutoAnterior.abs());
        BigDecimal diferenciaNeto = netoActual.subtract(netoAnterior.abs());
        BigDecimal porcentajeBruto = brutoAnterior.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : diferenciaBruto.multiply(BigDecimal.valueOf(100))
                        .divide(brutoAnterior, 2, RoundingMode.HALF_UP);

        return new Fila(text(persona.getLegajoId()), persona.apellidoNombre(),
                format(brutoAnterior), format(brutoActual), format(diferenciaBruto),
                format(porcentajeBruto) + "%", format(netoAnterior), format(netoActual),
                format(diferenciaNeto));
    }

    private Map<Long, LiquidacionRemuneracion> byLegajo(List<LiquidacionRemuneracion> liquidaciones) {
        return liquidaciones.stream()
                .filter(Objects::nonNull)
                .filter(liquidacion -> liquidacion.legajoId() != null)
                .collect(Collectors.toMap(LiquidacionRemuneracion::legajoId, l -> l, (a, b) -> a));
    }

    private BigDecimal value(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String format(BigDecimal value) {
        return FORMAT.format(value.doubleValue());
    }

    private String text(Long value) {
        return value == null ? "" : value.toString();
    }

    private String fileName(Integer anho, Integer mes, Integer anhoAnterior, Integer mesAnterior) {
        return "comparacion-remuneraciones." + anho + "." + mes + "." + anhoAnterior + "."
                + mesAnterior + ".pdf";
    }
}
