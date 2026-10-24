package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CursoCargoNovedadResumen;
import um.haberes.report.shared.domain.ports.out.CursoCargoNovedadCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CursoCargoNovedadClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoCargoNovedadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CursoCargoNovedadMapper;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignCursoCargoNovedadCatalogAdapter implements CursoCargoNovedadCatalogPort {

    private final CursoCargoNovedadClient cursoCargoNovedadClient;
    private final CursoCargoNovedadMapper cursoCargoNovedadMapper;

    @Override
    public List<CursoCargoNovedadResumen> findAllPendientes(Integer anho, Integer mes) {
        return map(cursoCargoNovedadClient.findAllPendientes(anho, mes));
    }

    @Override
    public List<CursoCargoNovedadResumen> findAllByPeriodo(Integer anho, Integer mes) {
        return map(cursoCargoNovedadClient.findAllByPeriodo(anho, mes));
    }

    @Override
    public List<CursoCargoNovedadResumen> findAllByFacultad(Integer facultadId, Integer anho, Integer mes) {
        return map(cursoCargoNovedadClient.findAllByFacultad(facultadId, anho, mes));
    }

    @Override
    public List<CursoCargoNovedadResumen> findAllByFacultadAndGeograficaAndAlta(
            Integer facultadId, Integer geograficaId, Integer anho, Integer mes) {
        return map(cursoCargoNovedadClient.findAllByFacultadAndGeograficaAndAlta(
                facultadId, geograficaId, anho, mes));
    }

    @Override
    public List<CursoCargoNovedadResumen> findAllByFacultadAndGeograficaAndCambio(
            Integer facultadId, Integer geograficaId, Integer anho, Integer mes) {
        return map(cursoCargoNovedadClient.findAllByFacultadAndGeograficaAndCambio(
                facultadId, geograficaId, anho, mes));
    }

    @Override
    public List<CursoCargoNovedadResumen> findAllByFacultadAndGeograficaAndBaja(
            Integer facultadId, Integer geograficaId, Integer anho, Integer mes) {
        return map(cursoCargoNovedadClient.findAllByFacultadAndGeograficaAndBaja(
                facultadId, geograficaId, anho, mes));
    }

    private List<CursoCargoNovedadResumen> map(List<CursoCargoNovedadDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(cursoCargoNovedadMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
