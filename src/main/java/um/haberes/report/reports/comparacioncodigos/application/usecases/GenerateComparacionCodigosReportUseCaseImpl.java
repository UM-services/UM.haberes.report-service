package um.haberes.report.reports.comparacioncodigos.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.comparacioncodigos.domain.model.ComparacionCodigosContent;
import um.haberes.report.reports.comparacioncodigos.domain.model.ComparacionCodigosContent.Fila;
import um.haberes.report.reports.comparacioncodigos.domain.ports.in.GenerateComparacionCodigosReportUseCase;
import um.haberes.report.reports.comparacioncodigos.domain.ports.out.ComparacionCodigosPdfRenderer;
import um.haberes.report.shared.domain.model.CodigoResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.model.TotalItemResumen;
import um.haberes.report.shared.domain.model.TotalNovedadResumen;
import um.haberes.report.shared.domain.ports.out.CodigoCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;
import um.haberes.report.shared.domain.ports.out.TotalesPeriodoPort;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenerateComparacionCodigosReportUseCaseImpl implements GenerateComparacionCodigosReportUseCase {

    static final String FILE_NAME = "comparacion_codigos.pdf";
    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(new Locale("en", "ES"));

    private final CodigoCatalogPort codigoCatalogPort;
    private final TotalesPeriodoPort totalesPeriodoPort;
    private final ComparacionCodigosPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        List<TotalNovedadResumen> novedades = totalesPeriodoPort.totalesNovedad(anho, mes);
        List<TotalItemResumen> items = totalesPeriodoPort.totalesItem(anho, mes);
        List<CodigoResumen> codigos = codigoCatalogPort.findAllByPeriodo(anho, mes);

        List<Fila> filas = contenido(novedades, items, codigos);
        Path target = storage.resolve(FILE_NAME);
        renderer.render(anho, mes, new ComparacionCodigosContent(filas), target);
        return storage.load(target, FILE_NAME, ReportFile.Format.PDF);
    }

    private List<Fila> contenido(List<TotalNovedadResumen> novedades, List<TotalItemResumen> items,
                                 List<CodigoResumen> codigos) {
        if (novedades.isEmpty() || items.isEmpty() || codigos.isEmpty()) {
            return List.of();
        }
        Map<Integer, BigDecimal> totalesNovedad = novedades.stream()
                .filter(Objects::nonNull)
                .filter(total -> total.getCodigoId() != null)
                .collect(Collectors.toMap(TotalNovedadResumen::getCodigoId,
                        total -> total.getTotal() == null ? BigDecimal.ZERO : total.getTotal(),
                        (primero, repetido) -> primero));
        Map<Integer, BigDecimal> totalesItem = items.stream()
                .filter(Objects::nonNull)
                .filter(total -> total.getCodigoId() != null)
                .collect(Collectors.toMap(TotalItemResumen::getCodigoId,
                        total -> total.getTotal() == null ? BigDecimal.ZERO : total.getTotal(),
                        (primero, repetido) -> primero));

        return codigos.stream()
                .filter(Objects::nonNull)
                .filter(codigo -> codigo.getCodigoId() != null)
                .sorted(Comparator.comparing(CodigoResumen::getCodigoId))
                .map(codigo -> fila(codigo,
                        totalesNovedad.getOrDefault(codigo.getCodigoId(), BigDecimal.ZERO),
                        totalesItem.getOrDefault(codigo.getCodigoId(), BigDecimal.ZERO)))
                .toList();
    }

    private Fila fila(CodigoResumen codigo, BigDecimal totalNovedad, BigDecimal totalItem) {
        boolean hayNovedad = totalNovedad.compareTo(BigDecimal.ZERO) != 0;
        return new Fila(
                codigo.getCodigoId(),
                codigo.getNombre(),
                hayNovedad ? FORMAT.format(totalNovedad) : "",
                FORMAT.format(totalItem),
                hayNovedad ? FORMAT.format(totalItem.subtract(totalNovedad).abs()) : "");
    }
}
