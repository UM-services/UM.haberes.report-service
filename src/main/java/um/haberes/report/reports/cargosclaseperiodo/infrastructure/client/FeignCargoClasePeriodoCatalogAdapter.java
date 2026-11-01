package um.haberes.report.reports.cargosclaseperiodo.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargoClasePeriodoVigencia;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.out.CargoClasePeriodoCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoClasePeriodoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClasePeriodoDto;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignCargoClasePeriodoCatalogAdapter implements CargoClasePeriodoCatalogPort {

    private final CargoClasePeriodoClient cargoClasePeriodoClient;
    private final CargosClasePeriodoMapper mapper;

    @Override
    public List<CargoClasePeriodoVigencia> findAllByLegajo(Long legajoId) {
        return map(cargoClasePeriodoClient.findAllByLegajo(legajoId));
    }

    @Override
    public List<CargoClasePeriodoVigencia> findAllByFacultad(Integer facultadId) {
        return map(cargoClasePeriodoClient.findAllByFacultad(facultadId));
    }

    private List<CargoClasePeriodoVigencia> map(List<CargoClasePeriodoDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(mapper::toVigencia).filter(Objects::nonNull).toList();
    }
}
