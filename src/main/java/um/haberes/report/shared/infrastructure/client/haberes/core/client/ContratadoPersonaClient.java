package um.haberes.report.shared.infrastructure.client.haberes.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ContratadoPersonaDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/contratadoPersona")
public interface ContratadoPersonaClient {

    @GetMapping("/")
    List<ContratadoPersonaDto> findAll();
}
