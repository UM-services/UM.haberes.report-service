package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CargoClaseDetalleResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDetalleDto;

@Component
public class CargoClaseDetalleMapper {

    public CargoClaseDetalleResumen toDomain(CargoClaseDetalleDto dto) {
        if (dto == null) {
            return null;
        }
        return CargoClaseDetalleResumen.builder()
                .legajoId(dto.getLegajoId())
                .anho(dto.getAnho())
                .mes(dto.getMes())
                .dependenciaId(dto.getDependenciaId())
                .dependenciaNombre(dto.getDependencia() == null ? null : dto.getDependencia().getNombre())
                .claseNombre(dto.getCargoClase() == null || dto.getCargoClase().getClase() == null
                        ? null : dto.getCargoClase().getClase().getNombre())
                .cargoClaseNombre(dto.getCargoClase() == null ? null : dto.getCargoClase().getNombre())
                .cargoClasePeriodoDescripcion(dto.getCargoClasePeriodo() == null
                        ? null : dto.getCargoClasePeriodo().getDescripcion())
                .valorHora(dto.getValorHora())
                .horas(dto.getHoras())
                .build();
    }
}
