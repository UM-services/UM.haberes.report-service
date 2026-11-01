package um.haberes.report.shared.infrastructure.client.tesoreria.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.shared.infrastructure.client.tesoreria.core.dto.PersonaDto;

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
