package um.haberes.report.shared.infrastructure.client.haberes.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FormularioImprimirDto;

/**
 * Formulario de impresión de bonos por dependencia: en una sola llamada devuelve los legajos
 * (con persona y flag de enviado) y los contactos (con mail institucional).
 */
@FeignClient(name = "haberes-core-service/api/haberes/core/formularioImprimir")
public interface FormularioImprimirClient {

    @GetMapping("/{anho}/{mes}/{dependenciaId}/{filtro}")
    FormularioImprimirDto findData(@PathVariable Integer anho, @PathVariable Integer mes,
                                   @PathVariable Integer dependenciaId, @PathVariable String filtro);
}
