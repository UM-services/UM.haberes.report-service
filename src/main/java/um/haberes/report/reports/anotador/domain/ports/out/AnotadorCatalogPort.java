package um.haberes.report.reports.anotador.domain.ports.out;

import um.haberes.report.reports.anotador.domain.model.AnotadorRegistro;

import java.util.List;

public interface AnotadorCatalogPort {

    List<AnotadorRegistro> findPendientes(Integer anho, Integer mes);

    List<AnotadorRegistro> findPendientesByFacultad(Integer facultadId, Integer anho, Integer mes);

    List<AnotadorRegistro> findRevisados(Integer anho, Integer mes);

    List<AnotadorRegistro> findAutorizadosByFacultad(Integer facultadId, Integer anho, Integer mes);

    List<AnotadorRegistro> findRechazadosByFacultad(Integer facultadId, Integer anho, Integer mes);
}
