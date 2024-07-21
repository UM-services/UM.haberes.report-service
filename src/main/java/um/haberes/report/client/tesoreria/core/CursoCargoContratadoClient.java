package um.haberes.report.client.tesoreria.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.tesoreria.core.CursoCargoContratadoDto;

import java.util.List;

@FeignClient(name = "tesoreria-core-service/cursocargocontratado")
public interface CursoCargoContratadoClient {

    @GetMapping("/cursos/{contratadoId}/{anho}/{mes}/{contratoId}")
    List<CursoCargoContratadoDto> findAllByContratado(@PathVariable Long contratadoId, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Long contratoId);

    @GetMapping("/curso/{cursoId}/{anho}/{mes}")
    List<CursoCargoContratadoDto> findAllByCurso(@PathVariable Long cursoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/cursosContratado/{contratadoId}/{anho}/{mes}")
    List<CursoCargoContratadoDto> findAllByCursosContratado(@PathVariable Long contratadoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/{cursocargocontratadoId}")
    CursoCargoContratadoDto findByCursoCargo(@PathVariable Long cursocargocontratadoId);

    @GetMapping("/contratado/{cursoId}/{anho}/{mes}/{contratadoId}")
    CursoCargoContratadoDto findByContratado(@PathVariable Long cursoId, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Long contratadoId);

    @PostMapping("/")
    CursoCargoContratadoDto add(@RequestBody CursoCargoContratadoDto cursocargocontratado);

    @PutMapping("/{cursocargocontratadoId}")
    CursoCargoContratadoDto update(@RequestBody CursoCargoContratadoDto cursocargocontratado, @PathVariable Long cursocargocontratadoId);

    @DeleteMapping("/{cursocargocontratadoId}")
    void delete(@PathVariable Long cursocargocontratadoId);

}
