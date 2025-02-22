package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.CargoClaseDto;

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
