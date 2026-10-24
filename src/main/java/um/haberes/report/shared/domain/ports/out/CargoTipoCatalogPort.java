package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.CargoTipoResumen;

import java.util.List;
import java.util.Optional;

public interface CargoTipoCatalogPort {

    Optional<CargoTipoResumen> findById(Integer cargoTipoId);

    List<CargoTipoResumen> findAll();
}
