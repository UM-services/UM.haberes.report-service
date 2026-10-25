package um.haberes.report.reports.totalescodigo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.totalescodigo.domain.model.TotalesCodigoContent;
import um.haberes.report.reports.totalescodigo.domain.ports.in.BuildTotalesCodigoContentUseCase;
import um.haberes.report.shared.domain.model.CodigoResumen;
import um.haberes.report.shared.domain.ports.out.CodigoCatalogPort;
import um.haberes.report.shared.domain.ports.out.TotalesPeriodoPort;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BuildTotalesCodigoContentUseCaseImpl implements BuildTotalesCodigoContentUseCase {

    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(new Locale("en", "ES"));

    private final CodigoCatalogPort codigoCatalogPort;
    private final TotalesPeriodoPort totalesPeriodoPort;

    @Override
    public TotalesCodigoContent build(Integer anho, Integer mes) {
        var novedades = totalesPeriodoPort.totalesNovedad(anho, mes);
        var items = totalesPeriodoPort.totalesItem(anho, mes);
        var codigos = codigoCatalogPort.findAllByPeriodo(anho, mes);

        List<TotalesCodigoContent.Fila> filas = List.of();
        if (!novedades.isEmpty() && !items.isEmpty() && !codigos.isEmpty()) {
            Map<Integer, BigDecimal> totalesNovedad = novedades.stream().filter(Objects::nonNull)
                    .filter(t -> t.getCodigoId() != null)
                    .collect(Collectors.toMap(t -> t.getCodigoId(),
                            t -> t.getTotal() == null ? BigDecimal.ZERO : t.getTotal(), (a, b) -> a));
            Map<Integer, BigDecimal> totalesItem = items.stream().filter(Objects::nonNull)
                    .filter(t -> t.getCodigoId() != null)
                    .collect(Collectors.toMap(t -> t.getCodigoId(),
                            t -> t.getTotal() == null ? BigDecimal.ZERO : t.getTotal(), (a, b) -> a));
            filas = codigos.stream().filter(Objects::nonNull)
                    .filter(c -> c.getCodigoId() != null)
                    .sorted(Comparator.comparing(CodigoResumen::getCodigoId))
                    .map(codigo -> new TotalesCodigoContent.Fila(
                            codigo.getCodigoId(), codigo.getNombre(),
                            FORMAT.format(totalesNovedad.getOrDefault(codigo.getCodigoId(), BigDecimal.ZERO)),
                            FORMAT.format(totalesItem.getOrDefault(codigo.getCodigoId(), BigDecimal.ZERO)),
                            FORMAT.format(totalesItem.getOrDefault(codigo.getCodigoId(), BigDecimal.ZERO)
                                    .subtract(totalesNovedad.getOrDefault(codigo.getCodigoId(), BigDecimal.ZERO)).abs())))
                    .filter(fila -> !fila.novedades().equals(FORMAT.format(BigDecimal.ZERO)))
                    .toList();
        }
        return new TotalesCodigoContent(filas);
    }
}
