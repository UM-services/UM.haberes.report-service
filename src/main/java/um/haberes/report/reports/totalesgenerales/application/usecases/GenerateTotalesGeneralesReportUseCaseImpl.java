package um.haberes.report.reports.totalesgenerales.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.totalesgenerales.domain.model.CodigoGrupo;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalMensualCodigo;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalesGeneralesContent;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalesGeneralesContent.Fila;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalesGeneralesContent.Grupo;
import um.haberes.report.reports.totalesgenerales.domain.ports.in.GenerateTotalesGeneralesReportUseCase;
import um.haberes.report.reports.totalesgenerales.domain.ports.out.CodigoGrupoCatalogPort;
import um.haberes.report.reports.totalesgenerales.domain.ports.out.TotalMensualCatalogPort;
import um.haberes.report.reports.totalesgenerales.domain.ports.out.TotalesGeneralesPdfRenderer;
import um.haberes.report.shared.domain.model.CodigoResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.CodigoCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenerateTotalesGeneralesReportUseCaseImpl
        implements GenerateTotalesGeneralesReportUseCase {

    static final String DISPLAY_NAME = "totales_generales.pdf";
    private static final List<String> ORDER = List.of(
            "Remunerativo", "No Remunerativo", "Deducción", "Total");
    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(new Locale("en", "ES"));

    private final CodigoCatalogPort codigoCatalogPort;
    private final TotalMensualCatalogPort totalMensualCatalogPort;
    private final CodigoGrupoCatalogPort codigoGrupoCatalogPort;
    private final TotalesGeneralesPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes) {
        Map<Integer, BigDecimal> totales = totalMensualCatalogPort.findAllByPeriodo(anho, mes).stream()
                .filter(Objects::nonNull)
                .filter(total -> total.codigoId() != null)
                .collect(Collectors.toMap(TotalMensualCodigo::codigoId,
                        total -> total.total() == null ? BigDecimal.ZERO : total.total(),
                        (a, b) -> a));

        List<CodigoResumen> codigos = codigoCatalogPort.findAllByPeriodo(anho, mes);
        List<CodigoGrupo> grupos = codigoGrupoCatalogPort.findAll();

        Map<String, List<CodigoResumen>> codigosPorGrupo = new LinkedHashMap<>();
        for (CodigoGrupo grupo : grupos) {
            if (grupo == null || grupo.codigoId() == null) {
                continue;
            }
            List<CodigoResumen> codigosGrupo = codigos.stream()
                    .filter(Objects::nonNull)
                    .filter(codigo -> codigo.getCodigoId() != null
                            && codigo.getCodigoId().equals(grupo.codigoId()))
                    .sorted(Comparator.comparing(CodigoResumen::getCodigoId))
                    .toList();
            add(codigosPorGrupo, "Remunerativo", grupo.remunerativo(), codigosGrupo);
            add(codigosPorGrupo, "No Remunerativo", grupo.noRemunerativo(), codigosGrupo);
            add(codigosPorGrupo, "Deducción", grupo.deduccion(), codigosGrupo);
            add(codigosPorGrupo, "Total", grupo.total(), codigosGrupo);
        }

        List<Grupo> content = new ArrayList<>();
        for (String nombreGrupo : ORDER) {
            List<CodigoResumen> codigosDelGrupo = codigosPorGrupo.get(nombreGrupo);
            if (codigosDelGrupo == null) {
                continue;
            }
            BigDecimal subtotal = BigDecimal.ZERO;
            List<Fila> filas = new ArrayList<>();
            for (CodigoResumen codigo : codigosDelGrupo) {
                BigDecimal total = totales.get(codigo.getCodigoId());
                if (total != null) {
                    subtotal = subtotal.add(total);
                }
                filas.add(new Fila(String.valueOf(codigo.getCodigoId()), codigo.getNombre(),
                        total == null ? "" : total.toString()));
            }
            content.add(new Grupo(nombreGrupo, filas, FORMAT.format(subtotal)));
        }

        Path target = storage.resolve(fileName(anho, mes));
        renderer.render(new TotalesGeneralesContent(anho, mes, content), target);
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.PDF);
    }

    private void add(Map<String, List<CodigoResumen>> codigosPorGrupo, String nombreGrupo,
                     boolean belongs, List<CodigoResumen> codigosGrupo) {
        if (belongs) {
            codigosPorGrupo.computeIfAbsent(nombreGrupo, key -> new ArrayList<>()).addAll(codigosGrupo);
        }
    }

    private String fileName(Integer anho, Integer mes) {
        return "totales.generales.." + anho + "." + mes + ".pdf";
    }
}
