package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.CursoFusionDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/cursofusion")
public interface CursoFusionClient {
    @GetMapping("/legajo/{legajoId}/{anho}/{mes}")
    List<CursoFusionDto> findAllByLegajoId(@PathVariable Long legajoId, @PathVariable Integer anho,
                                                               @PathVariable Integer mes);

    @GetMapping("/legajofacultad/{legajoId}/{anho}/{mes}/{facultadId}")
    List<CursoFusionDto> findAllByLegajoIdAndFacultadId(@PathVariable Long legajoId, @PathVariable Integer anho,
                                                        @PathVariable Integer mes, @PathVariable Integer facultadId);

    @PostMapping("/")
    CursoFusionDto add(@RequestBody CursoFusionDto cursofusion);

    @DeleteMapping("/{cursoFusionId}")
    void deleteByCursoFusionId(@PathVariable Long cursoFusionId);

    @DeleteMapping("/facultad/{legajoId}/{anho}/{mes}/{facultadId}/{geograficaId}")
    void deleteByFacultadId(@PathVariable Long legajoId, @PathVariable Integer anho,
                                                   @PathVariable Integer mes, @PathVariable Integer facultadId, @PathVariable Integer geograficaId);
}
