package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.CargoClasePeriodoDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/cargoclaseperiodo")
public interface CargoClasePeriodoClient {

    @GetMapping("/facultad/{facultadId}")
    List<CargoClasePeriodoDto> findAllByFacultad(@PathVariable Integer facultadId);

    @GetMapping("/legajo/{legajoId}")
    List<CargoClasePeriodoDto> findAllByLegajo(@PathVariable Long legajoId);

    @GetMapping("/{cargoClasePeriodoId}")
    CargoClasePeriodoDto findByCargoclaseperiodoId(@PathVariable Long cargoClasePeriodoId);

    @PostMapping("/")
    CargoClasePeriodoDto add(@RequestBody CargoClasePeriodoDto cargoClasePeriodo);

    @PutMapping("/{cargoClasePeriodoId}")
    CargoClasePeriodoDto update(@RequestBody CargoClasePeriodoDto cargoClasePeriodo,
                                                    @PathVariable Long cargoClasePeriodoId) ;

    @DeleteMapping("/{cargoClasePeriodoId}")
    void delete(@PathVariable Long cargoClasePeriodoId);
}
