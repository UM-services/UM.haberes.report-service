package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.TotalItemResumen;
import um.haberes.report.shared.domain.model.TotalNovedadResumen;

import java.util.List;

public interface TotalesPeriodoPort {

    List<TotalNovedadResumen> totalesNovedad(Integer anho, Integer mes);

    List<TotalItemResumen> totalesItem(Integer anho, Integer mes);
}
