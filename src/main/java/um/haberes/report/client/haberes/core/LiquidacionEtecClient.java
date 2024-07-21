package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "haberes-core-service/api/haberes/core/liquidacion-etec")
public interface LiquidacionEtecClient {

    @GetMapping("/calcularPorcentajeAntiguedad/{legajoId}/{anho}/{mes}/{facultadId}/{geograficaId}")
    BigDecimal calcularPorcentajeAntiguedad(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer facultadId, @PathVariable Integer geograficaId);

}
