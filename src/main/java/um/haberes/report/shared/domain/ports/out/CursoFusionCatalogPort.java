package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.CursoFusionResumen;

import java.util.List;

public interface CursoFusionCatalogPort {

    List<CursoFusionResumen> findAllByLegajoId(Long legajoId, Integer anho, Integer mes);
}
