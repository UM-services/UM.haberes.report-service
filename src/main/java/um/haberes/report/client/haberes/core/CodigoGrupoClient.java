package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.CodigoGrupoDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/codigogrupo")
public interface CodigoGrupoClient {

    @GetMapping("/")
    List<CodigoGrupoDto> findAll();

    @GetMapping("/{codigoId}")
    CodigoGrupoDto findByCodigoId(@PathVariable Integer codigoId);

    @GetMapping("/noremunerativo/{noRemunerativo}")
    List<CodigoGrupoDto> findAllByNoRemunerativo(@PathVariable Byte noRemunerativo);

    @GetMapping("/remunerativo/{remunerativo}")
    List<CodigoGrupoDto> findAllByRemunerativo(@PathVariable Byte remunerativo);

    @GetMapping("/deduccion/{deduccion}")
    List<CodigoGrupoDto> findAllByDeduccion(@PathVariable Byte deduccion);

    @PostMapping("/")
    CodigoGrupoDto add(@RequestBody CodigoGrupoDto codigo);

    @PutMapping("/{codigoId}")
    CodigoGrupoDto update(@RequestBody CodigoGrupoDto codigogrupo, @PathVariable Integer codigoId);

}
