package um.haberes.report.reports.detallecodigo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.detallecodigo.domain.model.DetalleCodigoContent;
import um.haberes.report.reports.detallecodigo.domain.ports.in.BuildDetalleCodigoContentUseCase;
import um.haberes.report.reports.detallecodigo.domain.ports.in.GenerateDetalleCodigoReportUseCase;
import um.haberes.report.reports.detallecodigo.domain.ports.in.GenerateDetalleCodigoXlsxUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class DetalleCodigoService {

    private final BuildDetalleCodigoContentUseCase buildContent;
    private final GenerateDetalleCodigoReportUseCase generatePdf;
    private final GenerateDetalleCodigoXlsxUseCase generateXlsx;

    public DetalleCodigoContent content(Integer codigoId, Integer anho, Integer mes) {
        return buildContent.build(codigoId, anho, mes);
    }

    public ReportFile pdf(Integer codigoId, Integer anho, Integer mes) {
        return generatePdf.generate(codigoId, anho, mes);
    }

    public ReportFile xlsx(Integer codigoId, Integer anho, Integer mes) {
        return generateXlsx.generate(codigoId, anho, mes);
    }
}
