package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CargoLiquidacionResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoLiquidacionDto;

@Component
@RequiredArgsConstructor
public class CargoLiquidacionMapper {

    private final DependenciaMapper dependenciaMapper;
    private final CategoriaMapper categoriaMapper;

    public CargoLiquidacionResumen toDomain(CargoLiquidacionDto dto) {
        if (dto == null) {
            return null;
        }
        return CargoLiquidacionResumen.builder()
                .legajoId(dto.getLegajoId())
                .anho(dto.getAnho())
                .mes(dto.getMes())
                .dependenciaId(dto.getDependenciaId())
                .categoriaId(dto.getCategoriaId())
                .categoriaNombre(dto.getCategoriaNombre())
                .categoriaBasico(dto.getCategoriaBasico())
                .horasJornada(dto.getHorasJornada())
                .jornada(dto.getJornada())
                .presentismo(dto.getPresentismo())
                .situacion(dto.getSituacion())
                .fechaDesde(dto.getFechaDesde())
                .fechaHasta(dto.getFechaHasta())
                .dependencia(dependenciaMapper.toDomain(dto.getDependencia()))
                .categoria(categoriaMapper.toDomain(dto.getCategoria()))
                .build();
    }
}
