package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.CodigoDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/codigo")
public interface CodigoClient {

    @GetMapping("/")
    List<CodigoDto> findAll();

    @GetMapping("/periodo/{anho}/{mes}")
    List<CodigoDto> findAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/{codigoId}")
    CodigoDto findByCodigoId(@PathVariable Integer codigoId);

    @GetMapping("/last")
    CodigoDto findLast();

    @DeleteMapping("/{codigoId}")
    void delete(@PathVariable Integer codigoId);

    @PostMapping("/")
    CodigoDto add(@RequestBody CodigoDto codigo);

    @PutMapping("/{codigoId}")
    CodigoDto update(@RequestBody CodigoDto codigo, @PathVariable Integer codigoId);

    @PutMapping("/")
    List<CodigoDto> saveAll(@RequestBody List<CodigoDto> codigos);

}
