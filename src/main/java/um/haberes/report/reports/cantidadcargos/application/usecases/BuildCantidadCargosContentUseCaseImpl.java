package um.haberes.report.reports.cantidadcargos.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cantidadcargos.domain.model.CantidadCargosContent;
import um.haberes.report.reports.cantidadcargos.domain.model.CantidadCargosContent.Fila;
import um.haberes.report.reports.cantidadcargos.domain.ports.in.BuildCantidadCargosContentUseCase;
import um.haberes.report.shared.domain.model.FacultadResumen;
import um.haberes.report.shared.domain.model.GeograficaResumen;
import um.haberes.report.shared.domain.model.ItemResumen;
import um.haberes.report.shared.domain.model.LegajoCursoCantidadResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.ports.out.FacultadCatalogPort;
import um.haberes.report.shared.domain.ports.out.GeograficaCatalogPort;
import um.haberes.report.shared.domain.ports.out.ItemCatalogPort;
import um.haberes.report.shared.domain.ports.out.LegajoCursoCantidadCatalogPort;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Planilla de cantidad de cargos (legacy: {@code SheetService.generateLegajoCursoCantidad}).
 *
 * <p>Tres consultas fijas: la vista por período, el batch de personas y los importes del
 * código 96. El bruto se expone formateado igual que en el resto de las planillas.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BuildCantidadCargosContentUseCaseImpl implements BuildCantidadCargosContentUseCase {

    private static final Integer CODIGO_BRUTO = 96;
    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(new Locale("en", "ES"));

    private final LegajoCursoCantidadCatalogPort legajoCursoCantidadCatalogPort;
    private final PersonaCatalogPort personaCatalogPort;
    private final ItemCatalogPort itemCatalogPort;
    private final FacultadCatalogPort facultadCatalogPort;
    private final GeograficaCatalogPort geograficaCatalogPort;

    @Override
    public CantidadCargosContent build(Integer anho, Integer mes) {
        List<LegajoCursoCantidadResumen> cantidades = legajoCursoCantidadCatalogPort
                .findAllByPeriodo(anho, mes).stream()
                .filter(Objects::nonNull)
                .filter(fila -> fila.getLegajoId() != null)
                .toList();
        if (cantidades.isEmpty()) {
            return new CantidadCargosContent(anho, mes, List.of());
        }

        List<Long> legajoIds = cantidades.stream()
                .map(LegajoCursoCantidadResumen::getLegajoId)
                .distinct()
                .toList();
        Map<Long, PersonaResumen> personas = personaCatalogPort.findAllByLegajos(legajoIds).stream()
                .filter(Objects::nonNull)
                .filter(persona -> persona.getLegajoId() != null)
                .collect(Collectors.toMap(PersonaResumen::getLegajoId, persona -> persona, (a, b) -> a));
        Map<Long, BigDecimal> brutos = itemCatalogPort
                .findAllByCodigo(CODIGO_BRUTO, anho, mes).stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getLegajoId() != null)
                .collect(Collectors.toMap(ItemResumen::getLegajoId, ItemResumen::getImporte, (a, b) -> a));
        Map<Integer, FacultadResumen> facultades = facultadCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(facultad -> facultad.getFacultadId() != null)
                .collect(Collectors.toMap(FacultadResumen::getFacultadId, f -> f, (a, b) -> a));
        Map<Integer, GeograficaResumen> geograficas = geograficaCatalogPort.findAll().stream()
                .filter(Objects::nonNull)
                .filter(geografica -> geografica.getGeograficaId() != null)
                .collect(Collectors.toMap(GeograficaResumen::getGeograficaId, g -> g, (a, b) -> a));

        List<Fila> filas = new ArrayList<>();
        for (LegajoCursoCantidadResumen cantidad : cantidades) {
            PersonaResumen persona = personas.get(cantidad.getLegajoId());
            if (persona == null) {
                log.warn("Sin persona para el legajo {} en la planilla de cantidad de cargos",
                        cantidad.getLegajoId());
                continue;
            }
            FacultadResumen facultad = facultades.get(cantidad.getFacultadId());
            GeograficaResumen geografica = geograficas.get(cantidad.getGeograficaId());
            BigDecimal bruto = brutos.get(cantidad.getLegajoId());
            filas.add(new Fila(facultad == null ? "" : text(facultad.getNombre()),
                    geografica == null ? "" : text(geografica.getNombre()),
                    text(cantidad.getLegajoId()), persona.apellidoNombre(),
                    cantidad.getAnuales(), cantidad.getSemestre1(), cantidad.getSemestre2(),
                    bruto == null ? "" : FORMAT.format(bruto)));
        }
        return new CantidadCargosContent(anho, mes, filas);
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
