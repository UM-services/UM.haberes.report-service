package um.haberes.report.reports.cursoscontratado.domain.ports.in;

import um.haberes.report.reports.cursoscontratado.domain.model.CursosContratadoContent;

public interface BuildCursosContratadoContentUseCase {

    CursosContratadoContent build(Integer anho, Integer mes);
}
