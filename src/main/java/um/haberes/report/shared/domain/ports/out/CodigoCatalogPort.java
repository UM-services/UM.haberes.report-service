package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.CodigoResumen;

import java.util.List;
import java.util.Optional;

public interface CodigoCatalogPort {

    Optional<CodigoResumen> findById(Integer codigoId);

    List<CodigoResumen> findAllByPeriodo(Integer anho, Integer mes);
}
