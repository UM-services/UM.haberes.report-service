package um.haberes.report.shared.infrastructure.client.haberes.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LegajoCursoCantidadDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/legajocursoCantidad")
public interface LegajoCursoCantidadClient {

    @GetMapping("/periodo/{anho}/{mes}")
    List<LegajoCursoCantidadDto> findAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);
}
