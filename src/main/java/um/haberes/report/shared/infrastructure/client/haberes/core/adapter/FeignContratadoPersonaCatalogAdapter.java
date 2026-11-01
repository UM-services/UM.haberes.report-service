package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.ContratadoPersonaResumen;
import um.haberes.report.shared.domain.ports.out.ContratadoPersonaCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.ContratadoPersonaClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ContratadoPersonaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.ContratadoPersonaMapper;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignContratadoPersonaCatalogAdapter implements ContratadoPersonaCatalogPort {

    private final ContratadoPersonaClient contratadoPersonaClient;
    private final ContratadoPersonaMapper contratadoPersonaMapper;

    @Override
    public List<ContratadoPersonaResumen> findAll() {
        List<ContratadoPersonaDto> dtos = contratadoPersonaClient.findAll();
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(contratadoPersonaMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
