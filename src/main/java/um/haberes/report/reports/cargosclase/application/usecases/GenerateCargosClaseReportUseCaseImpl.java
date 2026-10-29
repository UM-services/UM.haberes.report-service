package um.haberes.report.reports.cargosclase.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cargosclase.domain.model.CargoClase;
import um.haberes.report.reports.cargosclase.domain.model.CargosClaseContent;
import um.haberes.report.reports.cargosclase.domain.model.CargosClaseContent.ClaseConCargos;
import um.haberes.report.reports.cargosclase.domain.ports.in.GenerateCargosClaseReportUseCase;
import um.haberes.report.reports.cargosclase.domain.ports.out.CargoClaseCatalogPort;
import um.haberes.report.reports.cargosclase.domain.ports.out.CargosClasePdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ClaseCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenerateCargosClaseReportUseCaseImpl implements GenerateCargosClaseReportUseCase {

    static final String FILE_NAME = "cargos-clase.pdf";

    private final ClaseCatalogPort claseCatalogPort;
    private final CargoClaseCatalogPort cargoClaseCatalogPort;
    private final CargosClasePdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate() {
        Map<Integer, List<CargoClase>> cargosPorClase = cargoClaseCatalogPort.findAll().stream()
                .filter(cargo -> cargo.claseId() != null)
                .collect(Collectors.groupingBy(
                        CargoClase::claseId,
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(Collectors.toList(), cargos -> cargos.stream()
                                .sorted(Comparator.comparing(CargoClase::nombre,
                                        Comparator.nullsLast(String::compareToIgnoreCase)))
                                .toList())));

        List<ClaseConCargos> clases = claseCatalogPort.findAll().stream()
                .map(clase -> new ClaseConCargos(clase,
                        cargosPorClase.getOrDefault(clase.getClaseId(), List.of())))
                .filter(clase -> !clase.cargos().isEmpty())
                .toList();

        Path target = storage.resolve(FILE_NAME);
        renderer.render(new CargosClaseContent(clases), target);
        return storage.load(target, FILE_NAME, ReportFile.Format.PDF);
    }
}
