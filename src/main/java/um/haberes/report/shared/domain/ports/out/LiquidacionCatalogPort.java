package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.LiquidacionResumen;

import java.util.List;

public interface LiquidacionCatalogPort {

    List<LiquidacionResumen> findAllByAcreditado(Integer anho, Integer mes);

    List<LiquidacionResumen> findAllByDependencia(Integer dependenciaId, Integer anho, Integer mes, String salida);
}
