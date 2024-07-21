package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.LegajoBancoDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/legajobanco")
public interface LegajoBancoClient {

    @GetMapping("/{legajoBancoId}")
    LegajoBancoDto findByLegajoBancoId(@PathVariable Long legajoBancoId);

    @GetMapping("/unique/{legajoId}/{anho}/{mes}/{cbu}")
    LegajoBancoDto findByUnique(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable String cbu);

    @GetMapping("/lastlegajo/{legajoId}")
    LegajoBancoDto findLastByLegajoId(@PathVariable Long legajoId);

    @GetMapping("/legajo/{legajoId}")
    List<LegajoBancoDto> findAllByLegajoId(@PathVariable Long legajoId);

    @GetMapping("/legajoperiodo/{legajoId}/{anho}/{mes}")
    List<LegajoBancoDto> findAllByLegajoPeriodo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/cbuprincipal/{legajoId}/{anho}/{mes}")
    LegajoBancoDto findLegajoCbuPrincipal(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/periodo/{anho}/{mes}")
    List<LegajoBancoDto> findAllPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/periodosantander/{legajoId}/{anho}/{mes}")
    List<LegajoBancoDto> findAllPeriodoSantander(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/periodootrosbancos/{legajoId}/{anho}/{mes}")
    List<LegajoBancoDto> findAllPeriodoOtrosBancos(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @PostMapping("/")
    LegajoBancoDto add(@RequestBody LegajoBancoDto legajobanco);

    @PutMapping("/{legajobancoId}")
    LegajoBancoDto update(@RequestBody LegajoBancoDto legajobanco, @PathVariable Long legajobancoId);

    @GetMapping("/santander/{salida}/{anho}/{mes}/{dependenciaId}")
    List<LegajoBancoDto> findAllSantander(@PathVariable String salida, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer dependenciaId);

    @GetMapping("/otrosbancos/{salida}/{anho}/{mes}/{dependenciaId}")
    List<LegajoBancoDto> findAllOtrosBancos(@PathVariable String salida, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer dependenciaId);

    @DeleteMapping("/periodo/{anho}/{mes}")
    void deleteAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

    @DeleteMapping("/{legajobancoId}")
    void delete(@PathVariable Long legajobancoId);

}
