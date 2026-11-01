package um.haberes.report.reports.bono.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateBonoReportUseCase {

    ReportFile generar(Long legajoId, Integer anho, Integer mes, Long legajoIdSolicitud,
                       String ipAddress, boolean auditar);

    String nombreArchivo(Long legajoId, Integer anho, Integer mes);
}
