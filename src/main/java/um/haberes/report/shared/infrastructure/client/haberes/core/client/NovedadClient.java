package um.haberes.report.shared.infrastructure.client.haberes.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.NovedadDto;

@FeignClient(name = "haberes-core-service/api/haberes/core/novedad")
public interface NovedadClient {

    // dependenciaId "null" es el literal que espera haberes-core para las novedades sin dependencia.
    @GetMapping("/unique/{legajoId}/{anho}/{mes}/{codigoId}/{dependenciaId}")
    NovedadDto findByUnique(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes,
                            @PathVariable Integer codigoId, @PathVariable String dependenciaId);

}
