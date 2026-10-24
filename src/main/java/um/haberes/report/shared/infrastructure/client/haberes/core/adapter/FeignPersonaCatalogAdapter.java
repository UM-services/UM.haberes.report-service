package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.PersonaResumen;
import um.haberes.report.shared.domain.ports.out.PersonaCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.PersonaClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.PersonaMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FeignPersonaCatalogAdapter implements PersonaCatalogPort {

    private final PersonaClient personaClient;
    private final PersonaMapper personaMapper;

    @Override
    public Optional<PersonaResumen> findByLegajoId(Long legajoId) {
        return Optional.ofNullable(personaMapper.toDomain(personaClient.findByLegajoId(legajoId)));
    }

    @Override
    public List<PersonaResumen> findAll() {
        return map(personaClient.findAll());
    }

    @Override
    public List<PersonaResumen> findAllByFacultad(Integer facultadId) {
        return map(personaClient.findAllByFacultad(facultadId));
    }

    @Override
    public List<PersonaResumen> findAllDocente(Integer anho, Integer mes) {
        return map(personaClient.findAllDocente(anho, mes));
    }

    @Override
    public List<PersonaResumen> findAllByLegajos(List<Long> legajoIds) {
        return map(personaClient.findAllLegajos(legajoIds));
    }

    private List<PersonaResumen> map(List<PersonaDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(personaMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
