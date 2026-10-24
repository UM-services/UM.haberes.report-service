package um.haberes.report.reports.totalesgenerales.domain.ports.out;

import um.haberes.report.reports.totalesgenerales.domain.model.CodigoGrupo;

import java.util.List;

public interface CodigoGrupoCatalogPort {

    List<CodigoGrupo> findAll();
}
