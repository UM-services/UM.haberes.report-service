package um.haberes.report.reports.designacionesdetalle.domain.ports.out;

import um.haberes.report.shared.domain.model.DesignacionTipoResumen;

import java.util.List;

public interface DesignacionTipoCatalogPort {

    List<DesignacionTipoResumen> findAll();
}
