package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.CursoCargoNovedadResumen;

import java.util.List;

public interface CursoCargoNovedadCatalogPort {

    List<CursoCargoNovedadResumen> findAllPendientes(Integer anho, Integer mes);

    List<CursoCargoNovedadResumen> findAllByPeriodo(Integer anho, Integer mes);

    List<CursoCargoNovedadResumen> findAllByFacultad(Integer facultadId, Integer anho, Integer mes);

    List<CursoCargoNovedadResumen> findAllByFacultadAndGeograficaAndAlta(
            Integer facultadId, Integer geograficaId, Integer anho, Integer mes);

    List<CursoCargoNovedadResumen> findAllByFacultadAndGeograficaAndCambio(
            Integer facultadId, Integer geograficaId, Integer anho, Integer mes);

    List<CursoCargoNovedadResumen> findAllByFacultadAndGeograficaAndBaja(
            Integer facultadId, Integer geograficaId, Integer anho, Integer mes);
}
