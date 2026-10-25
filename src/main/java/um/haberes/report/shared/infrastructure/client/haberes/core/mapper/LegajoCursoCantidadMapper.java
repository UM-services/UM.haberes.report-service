package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.LegajoCursoCantidadResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LegajoCursoCantidadDto;

@Component
public class LegajoCursoCantidadMapper {

    public LegajoCursoCantidadResumen toDomain(LegajoCursoCantidadDto dto) {
        if (dto == null) {
            return null;
        }
        return LegajoCursoCantidadResumen.builder()
                .legajoId(dto.getLegajoId())
                .anho(dto.getAnho())
                .mes(dto.getMes())
                .facultadId(dto.getFacultadId())
                .geograficaId(dto.getGeograficaId())
                .anuales(dto.getAnuales())
                .semestre1(dto.getSemestre1())
                .semestre2(dto.getSemestre2())
                .build();
    }
}
