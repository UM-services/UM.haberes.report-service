package um.haberes.report.reports.docentessede.domain.ports.out;

import um.haberes.report.reports.docentessede.domain.model.CursoCargoContratado;

import java.util.List;

public interface CursoCargoContratadoCatalogPort {

    List<CursoCargoContratado> findAllByCurso(Long cursoId, Integer anho, Integer mes);
}
