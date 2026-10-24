package um.haberes.report.reports.historicoasignacioncargos.infrastructure.render;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.PageSize;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.pdf.PdfHeader;
import um.haberes.report.infrastructure.render.pdf.PdfHeaderFooter;
import um.haberes.report.reports.historicoasignacioncargos.domain.model.HistoricoAsignacionCargosContent;
import um.haberes.report.reports.historicoasignacioncargos.domain.ports.out.HistoricoAsignacionCargosPdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class HistoricoAsignacionCargosPdfRendererImpl implements HistoricoAsignacionCargosPdfRenderer {

    @Override
    public void render(HistoricoAsignacionCargosContent content, Path target) {
        Document document = new Document(PageSize.A4);
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            PdfHeaderFooter event = new PdfHeaderFooter(PdfHeader.of(
                    "Histórico de Asignación de Cargos", null,
                    List.of("Categoria: " + content.categoriaId())));
            writer.setPageEvent(event);
            document.open();
            event.setHeader(document);
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar historicoAsignacionDeCargos.pdf", e);
        }
    }
}
