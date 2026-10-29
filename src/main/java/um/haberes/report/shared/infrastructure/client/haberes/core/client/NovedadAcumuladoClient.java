package um.haberes.report.shared.infrastructure.client.haberes.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.NovedadAcumuladoDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/novedadacumulado")
public interface NovedadAcumuladoClient {

    @GetMapping("/codigo/{codigoId}/{anho}/{mes}")
    List<NovedadAcumuladoDto> findAllByCodigo(@PathVariable Integer codigoId,
                                              @PathVariable Integer anho,
                                              @PathVariable Integer mes);
}
