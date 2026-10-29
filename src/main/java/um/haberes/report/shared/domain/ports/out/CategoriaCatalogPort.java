package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.CategoriaResumen;

import java.util.List;

public interface CategoriaCatalogPort {

    List<CategoriaResumen> findAll();
}
