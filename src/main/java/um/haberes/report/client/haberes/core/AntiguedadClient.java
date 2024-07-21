package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.AntiguedadDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/antiguedad")
public interface AntiguedadClient {

    @GetMapping("/unique/{legajoId}/{anho}/{mes}")
    AntiguedadDto findByUnique(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/periodo/{anho}/{mes}/{limit}")
    List<AntiguedadDto> findAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer limit);

    @PostMapping("/")
    AntiguedadDto add(@RequestBody AntiguedadDto antiguedad);

    @PutMapping("/{antiguedadId}")
    AntiguedadDto update(@RequestBody AntiguedadDto antiguedad, @PathVariable Long antiguedadId);

    @PutMapping("/")
    List<AntiguedadDto> saveAll(@RequestBody List<AntiguedadDto> antiguedades);

    @GetMapping("/calculate/{legajoId}/{anho}/{mes}")
    void calculate(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

}
