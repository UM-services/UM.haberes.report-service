package um.haberes.report.reports.docentessede.domain.ports.out;

import um.haberes.report.reports.docentessede.domain.model.PersonaTesoreria;

import java.math.BigDecimal;
import java.util.Optional;

public interface PersonaTesoreriaCatalogPort {

    Optional<PersonaTesoreria> findByUnique(BigDecimal personaId, Integer documentoId);
}
