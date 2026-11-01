package um.haberes.report.reports.totalesgenerales.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalMensualCodigo;
import um.haberes.report.reports.totalesgenerales.domain.ports.out.TotalMensualCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.TotalMensualClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.TotalMensualDto;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignTotalMensualCatalogAdapter implements TotalMensualCatalogPort {

    private final TotalMensualClient totalMensualClient;
    private final TotalesGeneralesMapper mapper;

    @Override
    public List<TotalMensualCodigo> findAllByPeriodo(Integer anho, Integer mes) {
        List<TotalMensualDto> dtos = totalMensualClient.findAllByPeriodo(anho, mes);
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(mapper::toTotalMensual).filter(Objects::nonNull).toList();
    }
}
