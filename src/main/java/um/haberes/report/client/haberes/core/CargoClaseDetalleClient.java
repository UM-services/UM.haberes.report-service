package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.CargoClaseDetalleDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/cargoclasedetalle")
public interface CargoClaseDetalleClient {

    @GetMapping("/legajo/{legajoId}/{anho}/{mes}")
    List<CargoClaseDetalleDto> findAllByLegajo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/legajo/{legajoId}/{anho}/{mes}/facultad/{facultadId}")
    List<CargoClaseDetalleDto> findAllByLegajoAndFacultad(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer facultadId);

    @GetMapping("/facultad/{facultadId}/{anho}/{mes}")
    List<CargoClaseDetalleDto> findAllByFacultad(@PathVariable Integer facultadId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/cargoclaseperiodo/{cargoclaseperiodoId}")
    List<CargoClaseDetalleDto> findAllByCargoclaseperiodo(@PathVariable Long cargoclaseperiodoId);

    @GetMapping("/cargoclase/{cargoclaseId}/{anho}/{mes}")
    List<CargoClaseDetalleDto> findAllByCargoclase(@PathVariable Long cargoclaseId, @PathVariable Integer anho, @PathVariable Integer mes);

    @PostMapping("/")
    CargoClaseDetalleDto add(@RequestBody CargoClaseDetalleDto cargoclasedetalle);

    @PutMapping("/{cargoclasedetalleId}")
    CargoClaseDetalleDto update(@RequestBody CargoClaseDetalleDto cargoclasedetalle, @PathVariable Long cargoclasedetalleId);

    @PutMapping("/")
    List<CargoClaseDetalleDto> saveall(@RequestBody List<CargoClaseDetalleDto> cargoclasedetalles);

    @DeleteMapping("/{cargoClaseDetalleId}")
    void delete(@PathVariable Long cargoClaseDetalleId);

}
