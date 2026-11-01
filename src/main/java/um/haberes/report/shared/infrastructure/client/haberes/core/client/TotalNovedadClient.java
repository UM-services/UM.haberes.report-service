package um.haberes.report.shared.infrastructure.client.haberes.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.TotalNovedadDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/totalnovedad")
public interface TotalNovedadClient {

    @GetMapping("/periodo/{anho}/{mes}")
    List<TotalNovedadDto> findAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);
}
