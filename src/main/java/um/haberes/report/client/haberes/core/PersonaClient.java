package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.PersonaDto;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/persona")
public interface PersonaClient {

    @GetMapping("/")
    List<PersonaDto> findAll();

    @PostMapping("/legajos")
    List<PersonaDto> findAllLegajos(@RequestBody List<Long> legajos);

    @GetMapping("/docente/{anho}/{mes}")
    List<PersonaDto> findAllDocente(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/nodocente/{anho}/{mes}")
    List<PersonaDto> findAllNoDocente(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/semestre/{anho}/{semestre}")
    List<PersonaDto> findAllBySemestre(@PathVariable Integer anho, @PathVariable Integer semestre);

    @GetMapping("/desarraigo/{anho}/{mes}")
    List<PersonaDto> findAllByDesarraigo(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/liquidables")
    List<PersonaDto> findAllLiquidables();

    @GetMapping("/facultad/{facultadId}")
    List<PersonaDto> findAllByFacultad(@PathVariable Integer facultadId);

    @GetMapping("/{legajoId}")
    PersonaDto findByLegajoId(@PathVariable Long legajoId);

    @GetMapping("/documento/{documento}")
    PersonaDto findByDocumento(@PathVariable BigDecimal documento);

}
