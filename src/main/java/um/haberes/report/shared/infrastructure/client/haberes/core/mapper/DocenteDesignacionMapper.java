package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.DocenteDesignacionResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DocenteDesignacionDto;

@Component
public class DocenteDesignacionMapper {

    public DocenteDesignacionResumen toDomain(DocenteDesignacionDto dto) {
        if (dto == null) {
            return null;
        }
        return DocenteDesignacionResumen.builder()
                .legajoId(dto.getLegajoId())
                .anho(dto.getAnho())
                .mes(dto.getMes())
                .facultadId(dto.getFacultadId())
                .geograficaId(dto.getGeograficaId())
                .espacio(dto.getEspacio())
                .horasSemanales(dto.getHorasSemanales())
                .cargo(dto.getCargo())
                .designacion(dto.getDesignacion())
                .horasDesignacion(dto.getHorasDesignacion())
                .anual(dto.getAnual())
                .semestre1(dto.getSemestre1())
                .semestre2(dto.getSemestre2())
                .build();
    }
}
