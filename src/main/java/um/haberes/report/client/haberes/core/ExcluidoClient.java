package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.ExcluidoDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/excluido")
public interface ExcluidoClient {
    @GetMapping("/periodo/{anho}/{mes}")
    List<ExcluidoDto> findAllByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/unique/{legajoId}/{anho}/{mes}")
    ExcluidoDto findByUnique(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @PostMapping("/")
    ExcluidoDto add(@RequestBody ExcluidoDto excluido);

    @PutMapping("/{excluidoId}")
   ExcluidoDto update(@RequestBody ExcluidoDto excluido, @PathVariable Long excluidoId);

    @DeleteMapping("/unique/{legajoId}/{anho}/{mes}")
    void deleteByUnique(@PathVariable Long legajoId, @PathVariable Integer anho,
                                               @PathVariable Integer mes);

}
