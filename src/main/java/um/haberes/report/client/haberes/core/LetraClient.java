package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.LetraDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/letra")
public interface LetraClient {

    @GetMapping("/periodo/{anho}/{mes}/{limit}")
    List<LetraDto> findAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Integer limit);

    @GetMapping("/unique/{legajoId}/{anho}/{mes}")
    LetraDto findByUnique(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @PostMapping("/")
    LetraDto add(@RequestBody LetraDto letra);

    @PutMapping("/{letraId}")
    LetraDto update(@RequestBody LetraDto letra, @PathVariable Long letraId);

    @PutMapping("/")
    List<LetraDto> saveAll(@RequestBody List<LetraDto> letras);

    @DeleteMapping("/periodo/{anho}/{mes}")
    void deleteByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

}
