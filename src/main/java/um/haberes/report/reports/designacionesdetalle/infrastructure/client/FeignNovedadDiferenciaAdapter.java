package um.haberes.report.reports.designacionesdetalle.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.designacionesdetalle.domain.ports.out.NovedadDiferenciaPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.NovedadClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.NovedadDto;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FeignNovedadDiferenciaAdapter implements NovedadDiferenciaPort {

    private final NovedadClient novedadClient;

    @Override
    public Optional<BigDecimal> findImporte(Long legajoId, Integer anho, Integer mes,
                                            Integer codigoId, String dependenciaId) {
        NovedadDto novedad = novedadClient.findByUnique(legajoId, anho, mes, codigoId,
                dependenciaId);
        if (novedad == null || novedad.getImporte() == null) {
            return Optional.empty();
        }
        return Optional.of(novedad.getImporte());
    }
}
