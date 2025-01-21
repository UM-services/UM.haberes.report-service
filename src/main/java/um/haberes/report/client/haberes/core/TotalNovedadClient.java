package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.kotlin.dto.haberes.core.TotalNovedadDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/totalnovedad")
public interface TotalNovedadClient {

    @GetMapping("/periodo/{anho}/{mes}")
    List<TotalNovedadDto> findAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);
}
