package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CargoClaseDetalleResumen;
import um.haberes.report.shared.domain.ports.out.CargoClaseDetalleCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoClaseDetalleClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDetalleDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CargoClaseDetalleMapper;

import java.util.List;
import java.util.Objects;

/**
 * Adaptador del período para los cargos con clase. El slice {@code cargosclaseperiodo} tiene
 * su propio adaptador por facultad/legajo; este cubre la consulta global de la planilla de
 * básicos.
 */
@Component
@RequiredArgsConstructor
public class FeignCargoClaseDetallePeriodoAdapter implements CargoClaseDetalleCatalogPort {

    private final CargoClaseDetalleClient cargoClaseDetalleClient;
    private final CargoClaseDetalleMapper cargoClaseDetalleMapper;

    @Override
    public List<CargoClaseDetalleResumen> findAllByPeriodo(Integer anho, Integer mes) {
        List<CargoClaseDetalleDto> dtos = cargoClaseDetalleClient.findAllByPeriodo(anho, mes);
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(cargoClaseDetalleMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
