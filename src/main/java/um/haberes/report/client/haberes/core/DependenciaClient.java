package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.kotlin.dto.haberes.core.DependenciaDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/dependencia")
public interface DependenciaClient {

    @GetMapping("/")
    List<DependenciaDto> findAll();

    @GetMapping("/context/{facultadId}/{geograficaId}")
    List<DependenciaDto> findAllByFacultadIdAndGeograficaId(@PathVariable Integer facultadId, @PathVariable Integer geograficaId);

    @GetMapping("/{dependenciaId}")
    DependenciaDto findByDependenciaId(@PathVariable Integer dependenciaId);

    @GetMapping("/facultad/{facultadId}/{geograficaId}")
    DependenciaDto findFirstByFacultadIdAndGeograficaId(@PathVariable Integer facultadId, @PathVariable Integer geograficaId);

}
