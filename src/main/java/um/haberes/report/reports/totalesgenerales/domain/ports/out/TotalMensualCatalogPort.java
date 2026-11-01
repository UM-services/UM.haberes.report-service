package um.haberes.report.reports.totalesgenerales.domain.ports.out;

import um.haberes.report.reports.totalesgenerales.domain.model.TotalMensualCodigo;

import java.util.List;

public interface TotalMensualCatalogPort {

    List<TotalMensualCodigo> findAllByPeriodo(Integer anho, Integer mes);
}
