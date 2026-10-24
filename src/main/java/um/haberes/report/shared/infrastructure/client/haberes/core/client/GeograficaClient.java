package um.haberes.report.shared.infrastructure.client.haberes.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.GeograficaDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/geografica")
public interface GeograficaClient {

    @GetMapping("/")
    List<GeograficaDto> findAll();

    @PostMapping("/ids")
    List<GeograficaDto> findAllByGeograficaIdIn(@RequestBody List<Integer> ids);

    @GetMapping("/{geograficaId}")
    GeograficaDto findByGeograficaId(@PathVariable Integer geograficaId);

    @PutMapping("/{geograficaId}")
    GeograficaDto update(@RequestBody GeograficaDto geografica, @PathVariable Integer geograficaId);

}
