package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.kotlin.dto.haberes.core.CargoTipoDto;

import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/cargotipo")
public interface CargoTipoClient {

    @GetMapping("/")
    List<CargoTipoDto> findAll();

    @GetMapping("/{cargoTipoId}")
    CargoTipoDto findByCargoTipoId(@PathVariable Integer cargoTipoId);

}
