package um.haberes.report.reports.detallecodigo.domain.ports.in;

import um.haberes.report.reports.detallecodigo.domain.model.DetalleCodigoContent;

public interface BuildDetalleCodigoContentUseCase {

    DetalleCodigoContent build(Integer codigoId, Integer anho, Integer mes);
}
