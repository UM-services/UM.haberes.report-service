package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.CategoriaDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/categoria")
public interface CategoriaClient {
    @GetMapping("/")
    List<CategoriaDto> findAll();

    @GetMapping("/nogrado")
    List<CategoriaDto> findAllNoGrado();

    @GetMapping("/nodocente/{anho}/{mes}")
    List<CategoriaDto> findAllNoDocente(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/nodocentelegajo/{legajoId}/{anho}/{mes}")
    List<CategoriaDto> findAllNoDocentesByLegajoId(@PathVariable Long legajoId,
                                                   @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/{categoriaId}")
    CategoriaDto findByCategoriaId(@PathVariable Integer categoriaId);

    @GetMapping("/last")
    CategoriaDto findLast();

    @DeleteMapping("/{categoriaId}")
    void delete(@PathVariable Integer categoriaId);

    @PostMapping("/{anho}/{mes}")
    CategoriaDto add(@RequestBody CategoriaDto categoria, @PathVariable Integer anho,
                                         @PathVariable Integer mes);

    @PutMapping("/{categoriaId}/{anho}/{mes}")
    CategoriaDto update(@RequestBody CategoriaDto categoria, @PathVariable Integer categoriaId,
                                            @PathVariable Integer anho, @PathVariable Integer mes);

    @PutMapping("/all/{anho}/{mes}")
    List<CategoriaDto> saveAll(@RequestBody List<CategoriaDto> categorias, @PathVariable Integer anho,
                                                   @PathVariable Integer mes);
}

