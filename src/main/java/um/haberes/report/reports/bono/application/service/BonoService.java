package um.haberes.report.reports.bono.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.haberes.report.reports.bono.domain.ports.in.GenerateBonoReportUseCase;
import um.haberes.report.reports.bono.domain.ports.in.GenerateBonosDependenciaUseCase;
import um.haberes.report.reports.bono.domain.ports.in.GenerateDetalleCargosUseCase;
import um.haberes.report.reports.bono.domain.ports.in.SendBonoUseCase;
import um.haberes.report.shared.domain.model.ReportFile;

@Service
@RequiredArgsConstructor
public class BonoService {

    private final GenerateBonoReportUseCase generateBono;
    private final GenerateDetalleCargosUseCase generateDetalle;
    private final GenerateBonosDependenciaUseCase generateDependencia;
    private final SendBonoUseCase sendBono;

    public ReportFile generatePdf(Long legajoId, Integer anho, Integer mes,
                                  Long legajoIdSolicitud, String ipAddress) {
        return generateBono.generar(legajoId, anho, mes, legajoIdSolicitud, ipAddress, true);
    }

    public ReportFile generatePdfUi(Long legajoId, Integer anho, Integer mes) {
        return generateBono.generar(legajoId, anho, mes, null, null, false);
    }

    public String nombreArchivoBono(Long legajoId, Integer anho, Integer mes) {
        return generateBono.nombreArchivo(legajoId, anho, mes);
    }

    public ReportFile generateDetalleCargosPdf(Long legajoId, Integer anho, Integer mes,
                                               Integer facultadId) {
        return generateDetalle.generar(legajoId, anho, mes, facultadId);
    }

    public ReportFile generatePdfDependencia(Integer anho, Integer mes, Integer dependenciaId,
                                             String salida, Long legajoIdSolicitud,
                                             String ipAddress) {
        return generateDependencia.generar(anho, mes, dependenciaId, salida, legajoIdSolicitud,
                ipAddress);
    }

    public String sendBono(Long legajoId, Integer anho, Integer mes, Long legajoIdSolicitud,
                           String ipAddress) {
        return sendBono.enviar(legajoId, anho, mes, legajoIdSolicitud, ipAddress, true);
    }

    public String sendBonoUi(Long legajoId, Integer anho, Integer mes) {
        return sendBono.enviar(legajoId, anho, mes, null, null, false);
    }
}
