package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.FacultadResumen;

import java.util.List;
import java.util.Optional;

public interface FacultadCatalogPort {

    Optional<FacultadResumen> findById(Integer facultadId);

    List<FacultadResumen> findAll();

    List<FacultadResumen> findAllFacultades();
}
