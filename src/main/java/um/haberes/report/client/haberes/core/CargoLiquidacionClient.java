package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.CargoLiquidacionDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/cargoliquidacion")
public interface CargoLiquidacionClient {

    @GetMapping("/legajo/{legajoId}/{anho}/{mes}")
    List<CargoLiquidacionDto> findAllByLegajo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/legajodocente/{legajoId}/{anho}/{mes}")
    List<CargoLiquidacionDto> findAllDocenteByLegajo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/legajodocente/{legajoId}/{anho}/{mes}/facultad/{facultadId}")
    List<CargoLiquidacionDto> findAllDocenteByLegajoAndFacultad(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer facultadId);

    @GetMapping("/legajonodocente/{legajoId}/{anho}/{mes}")
    List<CargoLiquidacionDto> findAllNoDocenteByLegajo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/legajonodocente/{legajoId}/{anho}/{mes}/facultad/{facultadId}")
    List<CargoLiquidacionDto> findAllNoDocenteByLegajoAndFacultad(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer facultadId);

    @GetMapping("/legajonodocentehist/{legajoId}")
    List<CargoLiquidacionDto> findAllNoDocenteHistByLegajo(@PathVariable Long legajoId);

    @GetMapping("/legajoadicionalhcs/{legajoId}/{anho}/{mes}")
    List<CargoLiquidacionDto> findAllAdicionalHCSByLegajo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/categorianodocente/{legajoId}/{anho}/{mes}/{categoriaId}")
    CargoLiquidacionDto findByCategoriaNoDocente(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer categoriaId);

    @GetMapping("/{cargoId}")
    CargoLiquidacionDto findByCargoId(@PathVariable Long cargoId);

    @PostMapping("/")
    CargoLiquidacionDto add(@RequestBody CargoLiquidacionDto cargoLiquidacion);

    @PutMapping("/{cargoLiquidacionId}")
    CargoLiquidacionDto update(@RequestBody CargoLiquidacionDto cargoLiquidacion, @PathVariable Long cargoLiquidacionId);

    @PutMapping("/saveall/{version}")
    List<CargoLiquidacionDto> saveall(@RequestBody List<CargoLiquidacionDto> cargos, @PathVariable Integer version);

    @DeleteMapping("/periodo/{anho}/{mes}")
    void deleteByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

}
