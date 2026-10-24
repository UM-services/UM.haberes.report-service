package um.haberes.report.reports.bono.domain.ports.in;

import um.haberes.report.shared.domain.model.ReportFile;

public interface GenerateBonosDependenciaUseCase {

    ReportFile generar(Integer anho, Integer mes, Integer dependenciaId, String salida,
                       Long legajoIdSolicitud, String ipAddress);
}
