package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CursoResumen;
import um.haberes.report.shared.domain.ports.out.CursoCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CursoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.CursoMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Cacheable("cursos")
@RequiredArgsConstructor
public class FeignCursoCatalogAdapter implements CursoCatalogPort {

    private final CursoClient cursoClient;
    private final CursoMapper cursoMapper;

    @Override
    public List<CursoResumen> findAll() {
        return map(cursoClient.findAll());
    }

    @Override
    public Optional<CursoResumen> findById(Long cursoId) {
        return Optional.ofNullable(cursoMapper.toDomain(cursoClient.findByCursoId(cursoId)));
    }

    @Override
    public List<CursoResumen> findAllByFacultadAndGeografica(Integer facultadId, Integer geograficaId) {
        return map(cursoClient.findAllByFacultadIdAndGeograficaId(facultadId, geograficaId));
    }

    @Override
    public List<CursoResumen> findAllByFacultadAndGeograficaAndPeriodo(
            Integer facultadId, Integer geograficaId, Integer anho, Integer mes) {
        return map(cursoClient.findAllByFacultadIdAndGeograficaIdAndAnhoAndMes(
                facultadId, geograficaId, anho, mes));
    }

    private List<CursoResumen> map(List<CursoDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(cursoMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
