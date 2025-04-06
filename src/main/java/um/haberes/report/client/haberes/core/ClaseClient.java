package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.haberes.report.kotlin.dto.haberes.core.ClaseDto;

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
