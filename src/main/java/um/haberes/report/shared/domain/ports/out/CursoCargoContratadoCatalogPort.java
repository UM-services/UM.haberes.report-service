package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.CursoCargoContratadoResumen;

import java.util.List;

public interface CursoCargoContratadoCatalogPort {

    List<CursoCargoContratadoResumen> findAllByPeriodo(Integer anho, Integer mes);
}
