package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.ContratadoPersonaResumen;

import java.util.List;

public interface ContratadoPersonaCatalogPort {

    List<ContratadoPersonaResumen> findAll();
}
