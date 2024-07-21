package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.kotlin.dto.haberes.core.DesignacionTipoDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/designaciontipo")
public interface DesignacionTipoClient {

    @GetMapping("/")
    List<DesignacionTipoDto> findAll();

    @GetMapping("/{designacionTipoId}")
    DesignacionTipoDto findByDesignacionTipoId(@PathVariable Integer designacionTipoId);

}
