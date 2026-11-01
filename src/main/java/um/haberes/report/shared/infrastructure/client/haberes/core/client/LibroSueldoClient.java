package um.haberes.report.shared.infrastructure.client.haberes.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/** Genera el ZIP del Libro de Sueldos Digital de AFIP en haberes-core. */
@FeignClient(name = "haberes-core-service/api/haberes/core/libroSueldo")
public interface LibroSueldoClient {

    @PostMapping("/generate/{anho}/{mes}")
    byte[] generate(@PathVariable Integer anho, @PathVariable Integer mes,
                    @RequestBody List<Long> legajoIds);
}
