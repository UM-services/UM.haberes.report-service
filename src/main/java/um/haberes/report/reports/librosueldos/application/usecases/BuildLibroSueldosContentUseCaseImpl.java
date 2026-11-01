package um.haberes.report.reports.librosueldos.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.librosueldos.domain.model.LibroSueldosContent;
import um.haberes.report.reports.librosueldos.domain.model.LibroSueldosContent.Fila;
import um.haberes.report.reports.librosueldos.domain.ports.in.BuildLibroSueldosContentUseCase;
import um.haberes.report.shared.domain.model.Importes;
import um.haberes.report.shared.domain.model.LiquidacionResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.ports.out.LiquidacionCatalogPort;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Libro de sueldos del período.
 *
 * <p>Una consulta de liquidaciones sin límite (el total por legajo ya viene calculado) y el
 * batch de personas para CUIL y dependencia, que la liquidación no trae.
 */
@Component
@RequiredArgsConstructor
public class BuildLibroSueldosContentUseCaseImpl implements BuildLibroSueldosContentUseCase {

    private static final int SIN_LIMITE = 0;

    private final LiquidacionCatalogPort liquidacionCatalogPort;
    private final PersonaCatalogPort personaCatalogPort;

    @Override
    public LibroSueldosContent build(Integer anho, Integer mes) {
        List<LiquidacionResumen> liquidaciones = liquidacionCatalogPort
                .findAllByPeriodo(anho, mes, SIN_LIMITE).stream()
                .filter(Objects::nonNull)
                .filter(liquidacion -> liquidacion.getLegajoId() != null)
                .toList();
        if (liquidaciones.isEmpty()) {
            return new LibroSueldosContent(anho, mes, List.of());
        }

        List<Long> legajoIds = liquidaciones.stream()
                .map(LiquidacionResumen::getLegajoId)
                .distinct()
                .toList();
        Map<Long, PersonaResumen> personas = personaCatalogPort.findAllByLegajos(legajoIds).stream()
                .filter(Objects::nonNull)
                .filter(persona -> persona.getLegajoId() != null)
                .collect(Collectors.toMap(PersonaResumen::getLegajoId, persona -> persona, (a, b) -> a));

        List<Fila> filas = new ArrayList<>();
        for (LiquidacionResumen liquidacion : liquidaciones) {
            PersonaResumen persona = personas.get(liquidacion.getLegajoId());
            filas.add(new Fila(text(liquidacion.getLegajoId()),
                    persona == null ? "" : persona.apellidoNombre(),
                    persona == null ? "" : text(persona.getCuil()),
                    persona == null ? "" : text(persona.getDependenciaNombre()),
                    Importes.format(liquidacion.getTotalRemunerativo()),
                    Importes.format(liquidacion.getTotalNoRemunerativo()),
                    Importes.format(liquidacion.getTotalDeduccion()),
                    Importes.format(liquidacion.getTotalNeto())));
        }
        return new LibroSueldosContent(anho, mes, filas);
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
