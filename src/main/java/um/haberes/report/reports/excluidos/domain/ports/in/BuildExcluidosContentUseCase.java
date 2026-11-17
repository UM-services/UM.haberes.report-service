package um.haberes.report.reports.excluidos.domain.ports.in;

import um.haberes.report.reports.excluidos.domain.model.ExcluidosContent;

public interface BuildExcluidosContentUseCase {

    ExcluidosContent build(Integer anho, Integer mes);
}
