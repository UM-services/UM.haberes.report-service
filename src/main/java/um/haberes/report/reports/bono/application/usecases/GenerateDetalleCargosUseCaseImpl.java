package um.haberes.report.reports.bono.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.bono.domain.model.BonoFuente;
import um.haberes.report.reports.bono.domain.model.DetalleCargosContent;
import um.haberes.report.reports.bono.domain.ports.in.GenerateDetalleCargosUseCase;
import um.haberes.report.reports.bono.domain.ports.out.BonoDataPort;
import um.haberes.report.reports.bono.domain.ports.out.DetalleCargosPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class GenerateDetalleCargosUseCaseImpl implements GenerateDetalleCargosUseCase {

    private final BonoDataPort dataPort;
    private final BonoContentAssembler assembler;
    private final DetalleCargosPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generar(Long legajoId, Integer anho, Integer mes, Integer facultadId) {
        BonoFuente fuente = dataPort.cargar(legajoId, anho, mes, facultadId);
        DetalleCargosContent content = assembler.detalle(fuente, legajoId, facultadId);
        Path target = storage.resolve(
                "cargos." + legajoId + "." + anho + "." + mes + "." + facultadId + ".pdf");
        renderer.render(content, target);
        return storage.load(target, "bono.pdf", ReportFile.Format.PDF);
    }
}
