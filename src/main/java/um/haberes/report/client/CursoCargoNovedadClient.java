package um.haberes.report.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.CursoCargoNovedadDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/cursocargonovedad")
public interface CursoCargoNovedadClient {

    @GetMapping("/pendiente/{anho}/{mes}")
    List<CursoCargoNovedadDto> findAllPendientes(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/pendientealta/{anho}/{mes}")
    List<CursoCargoNovedadDto> findAllPendientesAlta(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/cursopendientealta/{cursoId}/{anho}/{mes}")
    List<CursoCargoNovedadDto> findAllCursoPendientesAlta(@PathVariable Long cursoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/autorizadoalta/{anho}/{mes}")
    List<CursoCargoNovedadDto> findAllAutorizadosAlta(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/rechazadoalta/{anho}/{mes}")
    List<CursoCargoNovedadDto> findAllRechazadosAlta(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/pendientebaja/{anho}/{mes}")
    List<CursoCargoNovedadDto> findAllPendientesBaja(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/cursopendientebaja/{cursoId}/{anho}/{mes}")
    List<CursoCargoNovedadDto> findAllCursoPendientesBaja(@PathVariable Long cursoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/autorizadobaja/{anho}/{mes}")
    List<CursoCargoNovedadDto> findAllAutorizadosBaja(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/rechazadobaja/{anho}/{mes}")
    List<CursoCargoNovedadDto> findAllRechazadosBaja(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/autorizadolegajo/{legajoId}/{cursoId}/{anho}/{mes}")
    List<CursoCargoNovedadDto> findAllAutorizadosLegajo(@PathVariable Long legajoId, @PathVariable Long cursoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/rechazadolegajo/{legajoId}/{cursoId}/{anho}/{mes}")
    List<CursoCargoNovedadDto> findAllRechazadosLegajo(@PathVariable Long legajoId, @PathVariable Long cursoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/pendientelegajo/{legajoId}/{cursoId}/{anho}/{mes}")
    List<CursoCargoNovedadDto> findAllPendientesLegajo(@PathVariable Long legajoId, @PathVariable Long cursoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/facultad/{facultadId}/{anho}/{mes}")
    List<CursoCargoNovedadDto> findAllByFacultad(@PathVariable Integer facultadId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/facultad/{facultadId}/geografica/{geograficaId}/periodo/{anho}/{mes}/alta")
    List<CursoCargoNovedadDto> findAllByFacultadAndGeograficaAndAlta(@PathVariable Integer facultadId, @PathVariable Integer geograficaId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/facultad/{facultadId}/geografica/{geograficaId}/periodo/{anho}/{mes}/cambio")
    List<CursoCargoNovedadDto> findAllByFacultadAndGeograficaAndCambio(@PathVariable Integer facultadId, @PathVariable Integer geograficaId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/facultad/{facultadId}/geografica/{geograficaId}/periodo/{anho}/{mes}/baja")
    List<CursoCargoNovedadDto> findAllByFacultadAndGeograficaAndBaja(@PathVariable Integer facultadId, @PathVariable Integer geograficaId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/{cursoCargoNovedadId}")
    CursoCargoNovedadDto findByCursoCargoNovedadId(@PathVariable Long cursoCargoNovedadId);

    @GetMapping("/legajo/{legajoId}/{cursoId}/{anho}/{mes}")
    CursoCargoNovedadDto findByLegajo(@PathVariable Long legajoId, @PathVariable Long cursoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/unique/{cursoId}/{anho}/{mes}/{cargoTipoId}/{legajoId}")
    CursoCargoNovedadDto findByUnique(@PathVariable Long cursoId, @PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer cargoTipoId, @PathVariable Long legajoId);

    @PostMapping("/")
    CursoCargoNovedadDto add(@RequestBody CursoCargoNovedadDto cursoCargoNovedad);

    @PutMapping("/{cursoCargoNovedadId}")
    CursoCargoNovedadDto update(@RequestBody CursoCargoNovedadDto cursoCargoNovedad, @PathVariable Long cursoCargoNovedadId);

    @DeleteMapping("/legajoPendiente/{legajoId}/{cursoId}/{anho}/{mes}")
    Void deleteAllByLegajoPendiente(@PathVariable Long legajoId, @PathVariable Long cursoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @DeleteMapping("/{cursoCargoNovedadId}")
    Void delete(@PathVariable Long cursoCargoNovedadId);

}
