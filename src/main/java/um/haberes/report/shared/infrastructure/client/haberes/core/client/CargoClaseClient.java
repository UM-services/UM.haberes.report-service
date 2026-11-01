package um.haberes.report.shared.infrastructure.client.haberes.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/cargoclase")
public interface CargoClaseClient {

    @GetMapping("/")
    List<CargoClaseDto> findAll();

    @GetMapping("/{cargoClaseId}")
    CargoClaseDto findByCargoClaseId(@PathVariable Long cargoClaseId);

    @PostMapping("/")
    CargoClaseDto add(@RequestBody CargoClaseDto cargoClase) ;

    @PutMapping("/{cargoClaseId}")
    CargoClaseDto update(@RequestBody CargoClaseDto cargoClase, @PathVariable Long cargoClaseId);
}
