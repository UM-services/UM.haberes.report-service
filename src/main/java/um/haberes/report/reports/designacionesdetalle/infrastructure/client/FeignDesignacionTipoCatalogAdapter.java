package um.haberes.report.reports.designacionesdetalle.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.designacionesdetalle.domain.ports.out.DesignacionTipoCatalogPort;
import um.haberes.report.shared.domain.model.DesignacionTipoResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.DesignacionTipoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DesignacionTipoDto;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignDesignacionTipoCatalogAdapter implements DesignacionTipoCatalogPort {

    private final DesignacionTipoClient designacionTipoClient;
    private final DesignacionesDetalleDesignacionTipoMapper mapper;

    @Override
    public List<DesignacionTipoResumen> findAll() {
        List<DesignacionTipoDto> dtos = designacionTipoClient.findAll();
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(mapper::toDomain).filter(Objects::nonNull).toList();
    }
}
