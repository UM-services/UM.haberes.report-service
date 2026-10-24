package um.haberes.report.shared.infrastructure.client.haberes.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ClaseDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/clase")
public interface ClaseClient {

    @GetMapping("/")
    List<ClaseDto> findAll();

    @GetMapping("/{claseId}")
    ClaseDto findByClaseId(@PathVariable Integer claseId);

    @GetMapping("/last")
    ClaseDto findLast() ;

    /*
     * delete
     *
     */
    @DeleteMapping("/{claseId}")
    void delete(@PathVariable Integer claseId) ;

    /*
     * add
     *
     */
    @PostMapping("/")
    ClaseDto add(@RequestBody ClaseDto clase);

    /*
     * update
     *
     */
    @PutMapping("/{claseId}")
    ClaseDto update(@RequestBody ClaseDto newClase, @PathVariable Integer claseId);
}
