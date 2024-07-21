package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.CursoCargoDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/cursoCargo")
public interface CursoCargoClient {

    @GetMapping("/legajo/{legajoId}/{anho}/{mes}")
    List<CursoCargoDto> findAllByLegajo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/legajonivel/{legajoId}/{anho}/{mes}/{nivelId}")
    List<CursoCargoDto> findAllByLegajoAndNivel(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer nivelId);

    @GetMapping("/legajodesarraigo/{legajoId}/{anho}/{mes}")
    List<CursoCargoDto> findAllByLegajoDesarraigo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/curso/{cursoId}/{anho}/{mes}")
    List<CursoCargoDto> findAllByCurso(@PathVariable Long cursoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/facultad/{legajoId}/{anho}/{mes}/{facultadId}")
    List<CursoCargoDto> findAllByFacultad(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer facultadId);

    @GetMapping("/cargoTipo/{legajoId}/{anho}/{mes}/{facultadId}/{geograficaId}/{anual}/{semestre1}/{semestre2}/{cargoTipoId}")
    List<CursoCargoDto> findAllByCargoTipo(@PathVariable Long legajoId, @PathVariable Integer anho,
                                        @PathVariable Integer mes, @PathVariable Integer facultadId, @PathVariable Integer geograficaId,
                                        @PathVariable Byte anual, @PathVariable Byte semestre1, @PathVariable Byte semestre2,
                                        @PathVariable Integer cargoTipoId);

    @GetMapping("/cursoany/{cursoId}")
    List<CursoCargoDto> findAllByCursoAny(@PathVariable Long cursoId);

    @GetMapping("/periodoany/{anho}/{mes}")
    List<CursoCargoDto> findAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/{cursoCargoId}")
    CursoCargoDto findByCursoCargoId(@PathVariable Long cursoCargoId);

    @GetMapping("/unique/{cursoId}/{anho}/{mes}/{cargoTipoId}/{legajoId}")
    CursoCargoDto findByUnique(@PathVariable Long cursoId, @PathVariable Integer anho,
                            @PathVariable Integer mes, @PathVariable Integer cargoTipoId, @PathVariable Long legajoId);

    @GetMapping("/legajo/{cursoId}/{anho}/{mes}/{legajoId}")
    CursoCargoDto findByLegajoId(@PathVariable Long cursoId, @PathVariable Integer anho,
                              @PathVariable Integer mes, @PathVariable Long legajoId);

    @PostMapping("/")
    CursoCargoDto add(@RequestBody CursoCargoDto cursoCargo);

    @PutMapping("/{cursoCargoId}")
    CursoCargoDto update(@RequestBody CursoCargoDto cursoCargo, @PathVariable Long cursoCargoId);

    @DeleteMapping("/{cursoCargoId}")
    void deleteByCursoCargoId(@PathVariable Long cursoCargoId);

    @DeleteMapping("/unique/{cursoId}/{anho}/{mes}/{cargoTipoId}/{legajoId}")
    void deleteByUnique(@PathVariable Long cursoId, @PathVariable Integer anho,
                        @PathVariable Integer mes, @PathVariable Integer cargoTipoId, @PathVariable Long legajoId);
}