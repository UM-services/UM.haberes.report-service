package um.haberes.report.reports.historicoasignacioncargos.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.historicoasignacioncargos.domain.model.HistoricoAsignacionCargosContent;
import um.haberes.report.reports.historicoasignacioncargos.domain.ports.in.BuildHistoricoAsignacionCargosContentUseCase;

/**
 * Contenido del histórico: sólo la categoría. El cuerpo del reporte nunca se implementó en el
 * sistema previo, por lo que no se consultan catálogos ni se inventan filas.
 */
@Component
@RequiredArgsConstructor
public class BuildHistoricoAsignacionCargosContentUseCaseImpl
        implements BuildHistoricoAsignacionCargosContentUseCase {

    @Override
    public HistoricoAsignacionCargosContent build(Integer categoriaId) {
        return new HistoricoAsignacionCargosContent(categoriaId);
    }
}
