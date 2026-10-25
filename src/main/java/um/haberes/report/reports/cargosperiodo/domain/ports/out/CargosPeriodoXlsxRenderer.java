package um.haberes.report.reports.cargosperiodo.domain.ports.out;

import um.haberes.report.reports.cargosperiodo.domain.model.CargosPeriodoContent;

import java.nio.file.Path;

public interface CargosPeriodoXlsxRenderer {

    void render(CargosPeriodoContent content, Path target);
}
