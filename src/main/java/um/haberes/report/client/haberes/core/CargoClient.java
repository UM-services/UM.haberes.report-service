package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.CargoDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/cargo")
public interface CargoClient {

    @GetMapping("/legajo/{legajoId}")
    List<CargoDto> findAllByLegajoId(@PathVariable Long legajoId);

    @GetMapping("/{cargoId}")
    CargoDto findByCargoId(@PathVariable Long cargoId);

    @PostMapping("/")
    CargoDto add(@RequestBody CargoDto cargo);

    @PutMapping("/{cargoId}")
    CargoDto update(@RequestBody CargoDto cargo, @PathVariable Long cargoId) ;

    @DeleteMapping("/{cargoId}")
    void delete(@PathVariable Long cargoId);
}
