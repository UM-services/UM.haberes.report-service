package um.haberes.report.reports.bono.domain.ports.out;

import java.nio.file.Path;
import java.util.List;

/** Une los PDF individuales del lote por dependencia. */
public interface PdfMergePort {

    void merge(Path target, List<Path> sources);
}
