package um.haberes.report.reports.excluidos.application.usecases;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import um.haberes.report.reports.excluidos.domain.model.ExcluidosContent;
import um.haberes.report.reports.excluidos.domain.ports.in.BuildExcluidosContentUseCase;
import um.haberes.report.shared.domain.model.ExcluidoResumen;
import um.haberes.report.shared.domain.model.ItemResumen;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.ports.out.ExcluidoCatalogPort;
import um.haberes.report.shared.domain.ports.out.ItemCatalogPort;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildExcluidosContentUseCaseImpl implements BuildExcluidosContentUseCase {

    private static final Integer CODIGO_NETO = 99;
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ExcluidoCatalogPort excluidoCatalogPort;
    private final PersonaCatalogPort personaCatalogPort;
    private final ItemCatalogPort itemCatalogPort;

    @Override
    public ExcluidosContent build(Integer anho, Integer mes) {
        List<ExcluidoResumen> excluidos = excluidoCatalogPort.findAllByPeriodo(anho, mes).stream()
                .filter(Objects::nonNull)
                .filter(excluido -> excluido.getLegajoId() != null)
                .sorted((a, b) -> a.getLegajoId().compareTo(b.getLegajoId()))
                .toList();
        if (excluidos.isEmpty()) {
            return new ExcluidosContent(anho, mes, List.of());
        }

        List<Long> legajoIds = excluidos.stream().map(ExcluidoResumen::getLegajoId).distinct().toList();
        Map<Long, PersonaResumen> personas = personaCatalogPort.findAllByLegajos(legajoIds).stream()
                .filter(Objects::nonNull)
                .filter(persona -> persona.getLegajoId() != null)
                .collect(Collectors.toMap(PersonaResumen::getLegajoId, persona -> persona, (a, b) -> a));
        Map<Long, BigDecimal> importes = itemCatalogPort.findAllByCodigo(CODIGO_NETO, anho, mes).stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getLegajoId() != null)
                .collect(Collectors.toMap(ItemResumen::getLegajoId, ItemResumen::getImporte, (a, b) -> a));
        NumberFormat formato = NumberFormat.getNumberInstance(new Locale("en", "ES"));
        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);

        List<ExcluidosContent.Fila> filas = new ArrayList<>();
        for (ExcluidoResumen excluido : excluidos) {
            PersonaResumen persona = personas.get(excluido.getLegajoId());
            if (persona == null) {
                log.warn("Sin persona para el legajo excluido {}", excluido.getLegajoId());
            }
            BigDecimal importe = importes.get(excluido.getLegajoId());
            filas.add(new ExcluidosContent.Fila(
                    excluido.getLegajoId().toString(),
                    persona == null ? "" : persona.apellidoNombre(),
                    importe == null ? "" : formato.format(importe),
                    excluido.getObservaciones() == null ? "" : excluido.getObservaciones(),
                    excluido.getFecha() == null ? "" : FECHA.format(excluido.getFecha().toLocalDate())));
        }
        return new ExcluidosContent(anho, mes, filas);
    }
}
