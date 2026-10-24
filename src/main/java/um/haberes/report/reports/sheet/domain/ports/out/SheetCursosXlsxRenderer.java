package um.haberes.report.reports.sheet.domain.ports.out;

import um.haberes.report.reports.sheet.domain.model.SheetCursosContent;

import java.nio.file.Path;

public interface SheetCursosXlsxRenderer {

    void render(SheetCursosContent content, Path target);
}
