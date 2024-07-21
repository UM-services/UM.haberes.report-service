package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import um.haberes.report.kotlin.dto.haberes.core.ControlDto;

@FeignClient(name = "haberes-core-service/api/haberes/core/control")
public interface ControlClient {

    @GetMapping("/periodo/{anho}/{mes}")
    ControlDto findByPeriodo(@PathVariable Integer anho, @PathVariable Integer mes);

    @PostMapping("/")
    ControlDto add(@RequestBody ControlDto control);

    @PutMapping("/{controlId}")
    ControlDto update(@RequestBody ControlDto control, @PathVariable Long controlId);

}
