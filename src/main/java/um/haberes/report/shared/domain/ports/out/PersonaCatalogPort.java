package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.PersonaResumen;

import java.util.List;
import java.util.Optional;

public interface PersonaCatalogPort {

    Optional<PersonaResumen> findByLegajoId(Long legajoId);

    List<PersonaResumen> findAll();

    List<PersonaResumen> findAllByFacultad(Integer facultadId);

    List<PersonaResumen> findAllDocente(Integer anho, Integer mes);

    List<PersonaResumen> findAllByLegajos(List<Long> legajoIds);
}
