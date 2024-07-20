package um.haberes.report.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.AnotadorDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/anotador")
public interface AnotadorClient {

    @GetMapping("/legajo/{legajoId}")
    List<AnotadorDto> findAllByLegajo(@PathVariable Long legajoId);

    @GetMapping("/pendiente/{anho}/{mes}")
    List<AnotadorDto> findPendientes(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/pendientefiltro/{anho}/{mes}/{filtro}")
    List<AnotadorDto> findPendientesByFiltro(@PathVariable Integer anho, @PathVariable Integer mes,
                                          @PathVariable String filtro);

    @GetMapping("/pendientefacultad/{facultadId}/{anho}/{mes}")
    List<AnotadorDto> findPendientesByFacultad(@PathVariable Integer facultadId,
                                            @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/revisado/{anho}/{mes}")
    List<AnotadorDto> findRevisados(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/revisadofiltro/{anho}/{mes}/{filtro}")
    List<AnotadorDto> findRevisadosByFiltro(@PathVariable Integer anho, @PathVariable Integer mes,
                                         @PathVariable String filtro);

    @GetMapping("/revisadofacultad/{facultadId}/{anho}/{mes}")
    List<AnotadorDto> findRevisadosByFacultad(@PathVariable Integer facultadId,
                                           @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/autorizadofacultad/{facultadId}/{anho}/{mes}")
    List<AnotadorDto> findAutorizadosByFacultad(@PathVariable("facultadId") Integer facultadId,
                                             @PathVariable("anho") Integer anho,
                                             @PathVariable("mes") Integer mes);

    @GetMapping("/rechazadofacultad/{facultadId}/{anho}/{mes}")
    List<AnotadorDto> findRechazadosByFacultad(@PathVariable("facultadId") Integer facultadId,
                                            @PathVariable("anho") Integer anho,
                                            @PathVariable("mes") Integer mes);
    @GetMapping("/{anotadorId}")
    AnotadorDto findByAnotadorId(@PathVariable Long anotadorId);

    @PostMapping("/")
    AnotadorDto add(@RequestBody AnotadorDto anotador);

    @PutMapping("/{anotadorId}")
    AnotadorDto update(@RequestBody AnotadorDto anotador, @PathVariable Long anotadorId);

}