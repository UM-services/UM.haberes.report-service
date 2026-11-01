package um.haberes.report.reports.bono.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.bono.domain.model.BonosDependenciaContent;
import um.haberes.report.reports.bono.domain.ports.in.BuildBonosDependenciaContentUseCase;
import um.haberes.report.reports.bono.domain.ports.out.BonoDependenciaDataPort;

/** Contenido de la grilla: proyección directa del formulario de impresión del core. */
@Component
@RequiredArgsConstructor
public class BuildBonosDependenciaContentUseCaseImpl implements BuildBonosDependenciaContentUseCase {

    private final BonoDependenciaDataPort dataPort;

    @Override
    public BonosDependenciaContent build(Integer anho, Integer mes, Integer dependenciaId,
                                         String salida) {
        return new BonosDependenciaContent(anho, mes, dependenciaId, salida,
                dataPort.legajos(anho, mes, dependenciaId, salida));
    }
}
