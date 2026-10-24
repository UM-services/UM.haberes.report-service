package um.haberes.report.shared.infrastructure.client.haberes.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DesignacionTipoDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/designaciontipo")
public interface DesignacionTipoClient {

    @GetMapping("/")
    List<DesignacionTipoDto> findAll();

    @GetMapping("/{designacionTipoId}")
    DesignacionTipoDto findByDesignacionTipoId(@PathVariable Integer designacionTipoId);

}
