package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.DependenciaResumen;

import java.util.List;
import java.util.Optional;

public interface DependenciaCatalogPort {

    Optional<DependenciaResumen> findById(Integer dependenciaId);

    List<DependenciaResumen> findAll();
}
