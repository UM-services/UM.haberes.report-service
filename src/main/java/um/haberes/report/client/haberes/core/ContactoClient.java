package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.ContactoDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/contacto")
public interface ContactoClient {

    @GetMapping("/")
    List<ContactoDto> findAll();

    @PostMapping("/legajos")
    List<ContactoDto> findAllLegajos(@RequestBody List<Long> legajos);

    @GetMapping("/{legajoId}")
    ContactoDto findByLegajoId(@PathVariable Long legajoId);

    @PostMapping("/")
    ContactoDto add(@RequestBody ContactoDto contacto);

    @PutMapping("/{legajoId}")
    ContactoDto update(@RequestBody ContactoDto contacto, @PathVariable Long legajoId);

    @DeleteMapping("/{legajoId}")
    void delete(@PathVariable Long legajoId);
}
