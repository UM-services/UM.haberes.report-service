package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.ItemResumen;

import java.util.List;

public interface ItemCatalogPort {

    List<ItemResumen> findAllByCodigo(Integer codigoId, Integer anho, Integer mes);
}
