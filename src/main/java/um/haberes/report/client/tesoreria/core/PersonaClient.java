package um.haberes.report.client.tesoreria.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.kotlin.dto.tesoreria.core.PersonaDto;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "tesoreria-core-service/persona")
public interface PersonaClient {

    @GetMapping("/santander")
    List<PersonaDto> findAllSantander();

    @GetMapping("/unique/{personaId}/{documentoId}")
    PersonaDto findByUnique(@PathVariable BigDecimal personaId, @PathVariable Integer documentoId);

    @GetMapping("/bypersonaId/{personaId}")
    PersonaDto findByPersonaId(@PathVariable BigDecimal personaId);

    @GetMapping("/{uniqueId}")
    PersonaDto findByUniqueId(@PathVariable Long uniqueId);

}
