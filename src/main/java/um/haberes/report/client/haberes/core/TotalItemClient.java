package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.kotlin.dto.haberes.core.TotalItemDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/totalitem")
public interface TotalItemClient {
    @GetMapping("/periodo/{anho}/{mes}")
    List<TotalItemDto> findAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);
}

