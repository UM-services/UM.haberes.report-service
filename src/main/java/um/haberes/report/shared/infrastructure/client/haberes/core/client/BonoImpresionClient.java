package um.haberes.report.shared.infrastructure.client.haberes.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.BonoImpresionDto;

@FeignClient(name = "haberes-core-service/api/haberes/core/bonoimpresion")
public interface BonoImpresionClient {

    @PostMapping("/")
    BonoImpresionDto add(@RequestBody BonoImpresionDto bonoimpresion);

}
