package um.haberes.report.reports.bono.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.bono.domain.model.BonoFuente;
import um.haberes.report.reports.bono.domain.ports.in.GenerateBonoReportUseCase;
import um.haberes.report.reports.bono.domain.ports.in.GenerateBonosDependenciaUseCase;
import um.haberes.report.reports.bono.domain.ports.out.BonoDataPort;
import um.haberes.report.reports.bono.domain.ports.out.PdfMergePort;
import um.haberes.report.shared.application.exception.ReportException;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenerateBonosDependenciaUseCaseImpl implements GenerateBonosDependenciaUseCase {

    private final BonoDataPort dataPort;
    private final GenerateBonoReportUseCase generateBono;
    private final PdfMergePort pdfMerge;
    private final ReportStorage storage;

    @Override
    public ReportFile generar(Integer anho, Integer mes, Integer dependenciaId, String salida,
                              Long legajoIdSolicitud, String ipAddress) {
        List<BonoFuente.Liquidacion> liquidaciones = dataPort
                .liquidacionesDependencia(dependenciaId, anho, mes, salida);
        if (liquidaciones.isEmpty()) {
            throw new ReportException("No hay liquidaciones para la dependencia "
                    + dependenciaId + " en " + mes + "/" + anho);
        }
        List<Path> bonos = liquidaciones.stream()
                .map(liquidacion -> generateBono.generar(liquidacion.legajoId(), anho, mes,
                        legajoIdSolicitud, ipAddress, true).path())
                .toList();
        Path target = storage.resolve("dependencia." + dependenciaId + ".pdf");
        pdfMerge.merge(target, bonos);
        return storage.load(target, "bonos.pdf", ReportFile.Format.PDF);
    }
}
