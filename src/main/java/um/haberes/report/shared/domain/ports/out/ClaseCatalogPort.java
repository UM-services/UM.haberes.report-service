package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.ClaseResumen;

import java.util.List;

public interface ClaseCatalogPort {

    List<ClaseResumen> findAll();
}
