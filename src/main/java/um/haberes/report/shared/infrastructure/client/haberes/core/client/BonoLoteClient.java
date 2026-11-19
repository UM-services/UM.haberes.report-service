package um.haberes.report.shared.infrastructure.client.haberes.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.BonoLoteDto;

/**
 * Lote completo de bonos de una dependencia: una sola llamada reemplaza el recorrido legajo por
 * legajo que hacía el reporte. El core resuelve la limpieza de items en cero y devuelve los
 * catálogos compartidos una única vez.
 */
@FeignClient(name = "haberes-core-service/api/haberes/core/bono")
public interface BonoLoteClient {

    @PostMapping("/dependencia/{dependenciaId}/{anho}/{mes}/{salida}/lote")
    BonoLoteDto cargar(@PathVariable Integer dependenciaId, @PathVariable Integer anho,
                       @PathVariable Integer mes, @PathVariable String salida);
}
