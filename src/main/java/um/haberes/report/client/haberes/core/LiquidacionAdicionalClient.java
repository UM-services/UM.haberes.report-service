package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.LiquidacionAdicionalDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/liquidacion-adicional")
public interface LiquidacionAdicionalClient {

    @GetMapping("/legajo/{legajoId}/{anho}/{mes}")
    List<LiquidacionAdicionalDto> findAllByLegajo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/dependencia/{legajoId}/{anho}/{mes}/{dependenciaId}")
    LiquidacionAdicionalDto findByDependencia(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer dependenciaId);

    @DeleteMapping("/legajo/{legajoId}/{anho}/{mes}")
    void deleteAllByLegajo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @PostMapping("/")
    LiquidacionAdicionalDto add(@RequestBody LiquidacionAdicionalDto liquidacionAdicional);

}
