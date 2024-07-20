package um.haberes.report.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.CursoDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/curso")
public interface CursoClient {

    @GetMapping("/")
    List<CursoDto> findAll();

    @PostMapping("/geografica/{facultadId}/{geograficaId}")
    List<CursoDto> findAllByGeograficaAndConditions(@RequestBody List<String> conditions, @PathVariable Integer facultadId, @PathVariable Integer geograficaId);

    @GetMapping("/geograficasinfiltro/{facultadId}/{geograficaId}")
    List<CursoDto> findAllByFacultadIdAndGeograficaId(@PathVariable Integer facultadId, @PathVariable Integer geograficaId);

    @GetMapping("/facultad/{facultadId}/geografica/{geograficaId}/periodo/{anho}/{mes}")
    List<CursoDto> findAllByFacultadIdAndGeograficaIdAndAnhoAndMes(@PathVariable Integer facultadId, @PathVariable Integer geograficaId, @PathVariable Integer anho, @PathVariable Integer mes);

        @GetMapping("/{cursoId}")
    CursoDto findByCursoId(@PathVariable Long cursoId);

    @PostMapping("/")
    CursoDto add(@RequestBody CursoDto curso);

    @PutMapping("/{cursoId}")
    CursoDto update(@RequestBody CursoDto curso, @PathVariable Long cursoId);

    @DeleteMapping("/{cursoId}")
    Void deleteByCursoId(@PathVariable Long cursoId);

}
