package um.haberes.report.reports.bono.domain.ports.in;

import um.haberes.report.reports.bono.domain.model.BonosDependenciaContent;

public interface BuildBonosDependenciaContentUseCase {

    BonosDependenciaContent build(Integer anho, Integer mes, Integer dependenciaId, String salida);
}
