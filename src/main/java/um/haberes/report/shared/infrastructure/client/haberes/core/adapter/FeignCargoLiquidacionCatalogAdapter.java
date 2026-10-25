package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CargoLiquidacionResumen;
import um.haberes.report.shared.domain.ports.out.CargoLiquidacionCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CargoLiquidacionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoLiquidacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CargoLiquidacionMapper;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignCargoLiquidacionCatalogAdapter implements CargoLiquidacionCatalogPort {

    private final CargoLiquidacionClient cargoLiquidacionClient;
    private final CargoLiquidacionMapper cargoLiquidacionMapper;

    @Override
    public List<CargoLiquidacionResumen> findAllActivosByPeriodo(Integer anho, Integer mes) {
        return map(cargoLiquidacionClient.findAllActivosByPeriodo(anho, mes));
    }

    @Override
    public List<CargoLiquidacionResumen> findAllByPeriodo(Integer anho, Integer mes) {
        return map(cargoLiquidacionClient.findAllByPeriodo(anho, mes));
    }

    @Override
    public List<CargoLiquidacionResumen> findAllDocenteByLegajo(Long legajoId, Integer anho, Integer mes) {
        return map(cargoLiquidacionClient.findAllDocenteByLegajo(legajoId, anho, mes));
    }

    @Override
    public List<CargoLiquidacionResumen> findAllNoDocenteByLegajo(Long legajoId, Integer anho, Integer mes) {
        return map(cargoLiquidacionClient.findAllNoDocenteByLegajo(legajoId, anho, mes));
    }

    @Override
    public List<CargoLiquidacionResumen> findAllDocenteByLegajoAndFacultad(
            Long legajoId, Integer anho, Integer mes, Integer facultadId) {
        return map(cargoLiquidacionClient.findAllDocenteByLegajoAndFacultad(legajoId, anho, mes, facultadId));
    }

    @Override
    public List<CargoLiquidacionResumen> findAllNoDocenteByLegajoAndFacultad(
            Long legajoId, Integer anho, Integer mes, Integer facultadId) {
        return map(cargoLiquidacionClient.findAllNoDocenteByLegajoAndFacultad(legajoId, anho, mes, facultadId));
    }

    private List<CargoLiquidacionResumen> map(List<CargoLiquidacionDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(cargoLiquidacionMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
