package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.CursoResumen;

import java.util.List;
import java.util.Optional;

public interface CursoCatalogPort {

    List<CursoResumen> findAll();

    Optional<CursoResumen> findById(Long cursoId);

    List<CursoResumen> findAllByFacultadAndGeografica(Integer facultadId, Integer geograficaId);

    List<CursoResumen> findAllByFacultadAndGeograficaAndPeriodo(
            Integer facultadId, Integer geograficaId, Integer anho, Integer mes);
}
