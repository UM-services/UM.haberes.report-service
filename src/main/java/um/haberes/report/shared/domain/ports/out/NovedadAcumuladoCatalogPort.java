package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.NovedadAcumuladoResumen;

import java.util.List;

public interface NovedadAcumuladoCatalogPort {

    List<NovedadAcumuladoResumen> findAllByCodigo(Integer codigoId, Integer anho, Integer mes);
}
