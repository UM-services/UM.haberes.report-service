package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.GeograficaResumen;

import java.util.List;
import java.util.Optional;

public interface GeograficaCatalogPort {

    Optional<GeograficaResumen> findById(Integer geograficaId);

    List<GeograficaResumen> findAll();

    List<GeograficaResumen> findAllByIds(List<Integer> geograficaIds);
}
