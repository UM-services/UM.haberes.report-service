package um.haberes.report.reports.designacionesdetalle.infrastructure.client;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.DesignacionTipoResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DesignacionTipoDto;

@Component
public class DesignacionesDetalleDesignacionTipoMapper {

    public DesignacionTipoResumen toDomain(DesignacionTipoDto dto) {
        if (dto == null) {
            return null;
        }
        return DesignacionTipoResumen.builder()
                .designacionTipoId(dto.getDesignacionTipoId())
                .nombre(dto.getNombre())
                .build();
    }
}
