package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.kotlin.dto.haberes.core.DocenteDesignacionDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/docenteDesignacion")
public interface DocenteDesignacionClient {

    @GetMapping("/periodo/{anho}/{mes}")
    List<DocenteDesignacionDto> findAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);


    @GetMapping("/legajo/{legajoId}/{anho}/{mes}")
    List<DocenteDesignacionDto> findAllByLegajo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

}

