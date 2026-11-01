package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CursoCargoResumen;
import um.haberes.report.shared.domain.ports.out.CursoCargoCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CursoCargoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoCargoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CursoCargoMapper;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignCursoCargoCatalogAdapter implements CursoCargoCatalogPort {

    private final CursoCargoClient cursoCargoClient;
    private final CursoCargoMapper cursoCargoMapper;

    @Override
    public List<CursoCargoResumen> findAllByLegajo(Long legajoId, Integer anho, Integer mes) {
        return map(cursoCargoClient.findAllByLegajo(legajoId, anho, mes));
    }

    @Override
    public List<CursoCargoResumen> findAllByLegajoAndNivel(Long legajoId, Integer anho, Integer mes, Integer nivelId) {
        return map(cursoCargoClient.findAllByLegajoAndNivel(legajoId, anho, mes, nivelId));
    }

    @Override
    public List<CursoCargoResumen> findAllByCurso(Long cursoId, Integer anho, Integer mes) {
        return map(cursoCargoClient.findAllByCurso(cursoId, anho, mes));
    }

    @Override
    public List<CursoCargoResumen> findAllByFacultad(Long legajoId, Integer anho, Integer mes, Integer facultadId) {
        return map(cursoCargoClient.findAllByFacultad(legajoId, anho, mes, facultadId));
    }

    @Override
    public List<CursoCargoResumen> findAllByPeriodo(Integer anho, Integer mes) {
        return map(cursoCargoClient.findAllByPeriodo(anho, mes));
    }

    private List<CursoCargoResumen> map(List<CursoCargoDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(cursoCargoMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
