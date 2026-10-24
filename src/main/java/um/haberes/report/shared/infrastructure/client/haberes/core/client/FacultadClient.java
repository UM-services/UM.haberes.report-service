package um.haberes.report.shared.infrastructure.client.haberes.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/facultad")
public interface FacultadClient {

    @GetMapping("/")
    List<FacultadDto> findAll();

    @GetMapping("/facultades")
    List<FacultadDto> findAllFacultades();

    @GetMapping("/{facultadId}")
    FacultadDto findByFacultadId(@PathVariable Integer facultadId);

}
