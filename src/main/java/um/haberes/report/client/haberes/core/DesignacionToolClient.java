package um.haberes.report.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "haberes-core-service/api/haberes/core/designaciontool")
public interface DesignacionToolClient {

    @GetMapping("/convertirbylegajo/{legajoId}/{anho}/{mes}")
    void convertirGradoByLegajo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/redesignarbylegajo/{legajoId}/{anho}/{mes}")
    void redesignarGradoByLegajo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/fusionarbylegajo/{legajoId}/{anho}/{mes}")
    void fusionarGradoByLegajo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/desarraigobylegajo/{legajoId}/{anho}/{mes}")
    void desarraigoGradobylegajo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/duplicarbylegajo/{legajoId}/{anho}/{mes}")
    void duplicarByLegajo(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/deletezombies/{anho}/{mes}")
    void deleteZombies(@PathVariable Integer anho, @PathVariable Integer mes);

    @GetMapping("/indiceantiguedad/{legajoId}/{anho}/{mes}")
    List<BigDecimal> indiceAntiguedad(@PathVariable Long legajoId, @PathVariable Integer anho, @PathVariable Integer mes);

}
