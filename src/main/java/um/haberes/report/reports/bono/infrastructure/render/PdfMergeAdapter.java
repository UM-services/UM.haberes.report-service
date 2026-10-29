package um.haberes.report.reports.bono.infrastructure.render;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.pdf.PdfMerge;
import um.haberes.report.reports.bono.domain.ports.out.PdfMergePort;

import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PdfMergeAdapter implements PdfMergePort {

    private final PdfMerge pdfMerge;

    @Override
    public void merge(Path target, List<Path> sources) {
        pdfMerge.merge(target, sources);
    }
}
