package um.haberes.report.reports.cargosclaseperiodo.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoDetalle;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.out.CargoClaseDetalleCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoClaseDetalleClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDetalleDto;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignCargoClaseDetalleCatalogAdapter implements CargoClaseDetalleCatalogPort {

    private final CargoClaseDetalleClient cargoClaseDetalleClient;
    private final CargosClasePeriodoMapper mapper;

    @Override
    public List<CargoClasePeriodoDetalle> findByLegajo(Long legajoId, Integer anho, Integer mes) {
        return map(cargoClaseDetalleClient.findAllByLegajo(legajoId, anho, mes));
    }

    @Override
    public List<CargoClasePeriodoDetalle> findAllByFacultad(Integer facultadId, Integer anho,
                                                            Integer mes) {
        return map(cargoClaseDetalleClient.findAllByFacultad(facultadId, anho, mes));
    }

    private List<CargoClasePeriodoDetalle> map(List<CargoClaseDetalleDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(mapper::toDetalle).filter(Objects::nonNull).toList();
    }
}
