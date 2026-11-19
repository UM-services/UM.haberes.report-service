package um.haberes.report.shared.infrastructure.client.haberes.core.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ExcluidoDto;

@FeignClient(name = "haberes-core-service", contextId = "excluidoClient", path = "/api/haberes/core/excluido")
public interface ExcluidoClient {

    @GetMapping("/periodo/{anho}/{mes}")
    List<ExcluidoDto> findAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);
}
