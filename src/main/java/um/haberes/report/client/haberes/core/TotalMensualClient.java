package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.kotlin.dto.haberes.core.TotalMensualDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/totalmensual")
public interface TotalMensualClient {

    @GetMapping("/periodo/{anho}/{mes}")
    List<TotalMensualDto> findAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

}
