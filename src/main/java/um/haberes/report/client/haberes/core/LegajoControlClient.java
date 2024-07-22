package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.LegajoControlDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/legajocontrol")
public interface LegajoControlClient {

    @GetMapping("/periodo/{anho}/{mes}")
    List<LegajoControlDto> findAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/liquidado/{anho}/{mes}")
    List<LegajoControlDto> findAllLiquidadoByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/dependencia/{anho}/{mes}/{dependenciaId}/{filtro}")
    List<LegajoControlDto> findAllDependenciaByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer dependenciaId, @PathVariable String filtro);

    @GetMapping("/unique/{legajoId}/{anho}/{mes}")
    LegajoControlDto findByUnique(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @PostMapping("/")
    LegajoControlDto add(@RequestBody LegajoControlDto legajocontrol);

    @PutMapping("/{legajocontrolId}")
    LegajoControlDto update(@RequestBody LegajoControlDto legajocontrol, @PathVariable Long legajocontrolId);

    @PutMapping("/saveall")
    List<LegajoControlDto> saveAll(@RequestBody List<LegajoControlDto> controles);

}
