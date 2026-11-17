package um.haberes.report.reports.excluidos.infrastructure.render;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import um.haberes.report.infrastructure.render.pdf.PdfHeader;
import um.haberes.report.infrastructure.render.pdf.PdfHeaderFooter;
import um.haberes.report.infrastructure.render.pdf.PdfTables;
import um.haberes.report.reports.excluidos.domain.model.ExcluidosContent;
import um.haberes.report.reports.excluidos.domain.ports.out.ExcluidosPdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

@Component
public class ExcluidosPdfRendererImpl implements ExcluidosPdfRenderer {

    private static final float[] WIDTHS = {9, 22, 11, 30, 9};
    private static final List<String> HEADERS = List.of("Legajo", "Apellido, Nombre", "Importe",
            "Observaciones", "Fecha");

    @Override
    public void render(ExcluidosContent content, Path target) {
        Document document = new Document(PageSize.A4);
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            PdfHeaderFooter event = new PdfHeaderFooter(
                    PdfHeader.of("Excluidos", content.mes() + "/" + content.anho()));
            writer.setPageEvent(event);
            document.open();
            event.setHeader(document);

            PdfPTable table = new PdfPTable(HEADERS.size());
            table.setWidthPercentage(100);
            table.setWidths(WIDTHS);
            for (String title : HEADERS) {
                table.addCell(PdfTables.noBorder(title, PdfTables.bold(7), Element.ALIGN_LEFT));
            }
            if (content.filas().isEmpty()) {
                document.add(new Paragraph("Sin excluidos para el período.",
                        new Font(Font.HELVETICA, 10, Font.BOLD)));
            }
            for (ExcluidosContent.Fila fila : content.filas()) {
                table.addCell(PdfTables.noBorder(fila.legajoId(), PdfTables.font(7), Element.ALIGN_RIGHT));
                table.addCell(PdfTables.noBorder(fila.apellidoNombre(), PdfTables.font(7), Element.ALIGN_LEFT));
                table.addCell(PdfTables.noBorder(fila.importe(), PdfTables.font(7), Element.ALIGN_RIGHT));
                table.addCell(PdfTables.noBorder(fila.observaciones(), PdfTables.font(7), Element.ALIGN_LEFT));
                table.addCell(PdfTables.noBorder(fila.fecha(), PdfTables.font(7), Element.ALIGN_CENTER));
            }
            document.add(table);
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar excluidos.pdf", e);
        }
    }
}
