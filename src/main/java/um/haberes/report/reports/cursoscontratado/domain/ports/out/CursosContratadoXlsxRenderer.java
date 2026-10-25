package um.haberes.report.reports.cursoscontratado.domain.ports.out;

import um.haberes.report.reports.cursoscontratado.domain.model.CursosContratadoContent;

import java.nio.file.Path;

public interface CursosContratadoXlsxRenderer {

    void render(CursosContratadoContent content, Path target);
}
