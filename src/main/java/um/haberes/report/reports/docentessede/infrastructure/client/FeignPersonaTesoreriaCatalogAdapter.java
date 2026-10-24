package um.haberes.report.reports.docentessede.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.docentessede.domain.model.PersonaTesoreria;
import um.haberes.report.reports.docentessede.domain.ports.out.PersonaTesoreriaCatalogPort;
import um.haberes.report.shared.infrastructure.client.tesoreria.core.client.PersonaClient;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FeignPersonaTesoreriaCatalogAdapter implements PersonaTesoreriaCatalogPort {

    private final PersonaClient personaClient;
    private final DocentesSedeMapper mapper;

    @Override
    public Optional<PersonaTesoreria> findByUnique(BigDecimal personaId, Integer documentoId) {
        return Optional.ofNullable(
                mapper.toDomain(personaClient.findByUnique(personaId, documentoId)));
    }
}
