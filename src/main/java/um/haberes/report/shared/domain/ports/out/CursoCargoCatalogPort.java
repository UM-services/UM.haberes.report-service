package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.CursoCargoResumen;

import java.util.List;

public interface CursoCargoCatalogPort {

    List<CursoCargoResumen> findAllByLegajo(Long legajoId, Integer anho, Integer mes);

    List<CursoCargoResumen> findAllByLegajoAndNivel(Long legajoId, Integer anho, Integer mes, Integer nivelId);

    List<CursoCargoResumen> findAllByCurso(Long cursoId, Integer anho, Integer mes);

    List<CursoCargoResumen> findAllByFacultad(Long legajoId, Integer anho, Integer mes, Integer facultadId);

    List<CursoCargoResumen> findAllByPeriodo(Integer anho, Integer mes);
}
