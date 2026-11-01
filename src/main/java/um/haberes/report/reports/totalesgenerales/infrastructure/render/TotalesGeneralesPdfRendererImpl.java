package um.haberes.report.reports.totalesgenerales.infrastructure.render;

import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.pdf.PdfHeader;
import um.haberes.report.infrastructure.render.pdf.PdfHeaderFooter;
import um.haberes.report.infrastructure.render.pdf.PdfTables;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalesGeneralesContent;
import um.haberes.report.reports.totalesgenerales.domain.ports.out.TotalesGeneralesPdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class TotalesGeneralesPdfRendererImpl implements TotalesGeneralesPdfRenderer {

    @Override
    public void render(TotalesGeneralesContent content, Path target) {
        Document document = new Document(PageSize.A4);
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            PdfHeaderFooter event = new PdfHeaderFooter(
                    PdfHeader.of("Totales Generales", content.mes() + "/" + content.anho()));
            writer.setPageEvent(event);
            document.open();
            event.setHeader(document);

            if (content.grupos().isEmpty()) {
                document.add(new Paragraph("Sin datos para el período.",
                        new Font(Font.HELVETICA, 10, Font.BOLD)));
            }
            for (TotalesGeneralesContent.Grupo grupo : content.grupos()) {
                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(95);
                table.setWidths(new float[]{15, 70, 15});
                addHeader(table);
                for (TotalesGeneralesContent.Fila fila : grupo.filas()) {
                    table.addCell(PdfTables.noBorder(text(fila.codigoId()), PdfTables.font(8), Element.ALIGN_LEFT));
                    table.addCell(PdfTables.noBorder(text(fila.nombre()), PdfTables.font(8), Element.ALIGN_LEFT));
                    table.addCell(PdfTables.noBorder(text(fila.total()), PdfTables.font(8), Element.ALIGN_CENTER));
                }
                PdfPCell emptyCell = new PdfPCell(new Phrase(""));
                emptyCell.setBorder(Rectangle.NO_BORDER);
                PdfPCell subtotalLabelCell = new PdfPCell();
                subtotalLabelCell.setBorder(Rectangle.NO_BORDER);
                subtotalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                PdfPCell subtotalValueCell = new PdfPCell(
                        new Phrase(text(grupo.subtotal()), PdfTables.bold(8)));
                subtotalValueCell.setBorder(Rectangle.NO_BORDER);
                subtotalValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(emptyCell);
                table.addCell(subtotalLabelCell);
                table.addCell(subtotalValueCell);
                document.add(Chunk.NEWLINE);
                document.add(table);
                document.add(Chunk.NEWLINE);
            }
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar totales_generales.pdf", e);
        }
    }

    private void addHeader(PdfPTable table) {
        table.addCell(PdfTables.noBorder("Código", PdfTables.bold(8), Element.ALIGN_LEFT));
        table.addCell(PdfTables.noBorder("Descripción", PdfTables.bold(8), Element.ALIGN_LEFT));
        table.addCell(PdfTables.noBorder("Total", PdfTables.bold(8), Element.ALIGN_CENTER));
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
