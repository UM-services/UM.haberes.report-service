package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.DependenciaResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DependenciaDto;

@Component
public class DependenciaMapper {

    public DependenciaResumen toDomain(DependenciaDto dto) {
        if (dto == null) {
            return null;
        }
        return DependenciaResumen.builder()
                .dependenciaId(dto.getDependenciaId())
                .nombre(dto.getNombre())
                .acronimo(dto.getAcronimo())
                .facultadId(dto.getFacultadId())
                .geograficaId(dto.getGeograficaId())
                .build();
    }
}
