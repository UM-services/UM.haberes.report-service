package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.LiquidacionDto;

import java.time.OffsetDateTime;
import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/liquidacion")
public interface LiquidacionClient {

    @GetMapping("/periodo/{anho}/{mes}/{limit}")
    List<LiquidacionDto> findAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer limit);

    @GetMapping("/periodolegajo/{anho}/{mes}/{legajoId}/{limit}")
    List<LiquidacionDto> findAllByPeriodoLegajo(@PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Long legajoId, @PathVariable Integer limit);

    @GetMapping("/semestre/{anho}/{semestre}/{limit}")
    List<LiquidacionDto> findAllBySemestre(@PathVariable Integer anho, @PathVariable Integer semestre, @PathVariable Integer limit);

    @GetMapping("/semestrelegajo/{anho}/{semestre}/{legajoId}/{limit}")
    List<LiquidacionDto> findAllBySemestreLegajo(@PathVariable Integer anho, @PathVariable Integer semestre, @PathVariable Long legajoId, @PathVariable Integer limit);

    @GetMapping("/legajo/{legajoId}")
    List<LiquidacionDto> findAllByLegajo(@PathVariable Long legajoId);

    @GetMapping("/dependencia/{dependenciaId}/{anho}/{mes}/{salida}")
    List<LiquidacionDto> findAllByDependencia(@PathVariable Integer dependenciaId, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable String salida);

    @GetMapping("/acreditado/{anho}/{mes}")
    List<LiquidacionDto> findAllByAcreditado(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/{liquidacionId}")
    LiquidacionDto findByLiquidacionId(@PathVariable Long liquidacionId);

    @GetMapping("/unique/{legajoId}/{anho}/{mes}")
    LiquidacionDto findByUnique(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @PostMapping("/")
    LiquidacionDto add(@RequestBody LiquidacionDto liquidacion);

    @PostMapping("/version/{version}")
    LiquidacionDto addVersion(@RequestBody LiquidacionDto liquidacion, @PathVariable Integer version);

    @PostMapping("/acreditado/{fecha}")
    LiquidacionDto acreditado(@RequestBody LiquidacionDto liquidacion, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fecha);

    @PutMapping("/{liquidacionId}")
    LiquidacionDto update(@RequestBody LiquidacionDto liquidacion, @PathVariable Long liquidacionId);

    @PutMapping("/saveall/{version}")
    List<LiquidacionDto> saveall(@RequestBody List<LiquidacionDto> liquidaciones, @PathVariable Integer version);

    @PutMapping("/version/{liquidacionId}/{version}")
    LiquidacionDto updateVersion(@RequestBody LiquidacionDto liquidacion, @PathVariable Long liquidacionId, @PathVariable Integer version);

    @DeleteMapping("/periodo/{anho}/{mes}")
    void deleteByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

}
