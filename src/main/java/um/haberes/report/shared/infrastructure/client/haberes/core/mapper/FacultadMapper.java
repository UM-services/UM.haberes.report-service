package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.FacultadResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;

@Component
public class FacultadMapper {

    public FacultadResumen toDomain(FacultadDto dto) {
        if (dto == null) {
            return null;
        }
        return FacultadResumen.builder()
                .facultadId(dto.getFacultadId())
                .nombre(dto.getNombre())
                .build();
    }
}
