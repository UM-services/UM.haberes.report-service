package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.NovedadAcumuladoResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.NovedadAcumuladoDto;

@Component
public class NovedadAcumuladoMapper {

    public NovedadAcumuladoResumen toDomain(NovedadAcumuladoDto dto) {
        if (dto == null) {
            return null;
        }
        return NovedadAcumuladoResumen.builder()
                .legajoId(dto.getLegajoId())
                .codigoId(dto.getCodigoId())
                .anho(dto.getAnho())
                .mes(dto.getMes())
                .importe(dto.getImporte())
                .cantidad(dto.getCantidad())
                .build();
    }
}
