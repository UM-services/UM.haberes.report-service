package um.haberes.report.reports.totalescodigo.infrastructure.render;

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
import um.haberes.report.reports.totalescodigo.domain.model.TotalesCodigoContent;
import um.haberes.report.reports.totalescodigo.domain.ports.out.TotalesCodigoPdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class TotalesCodigoPdfRendererImpl implements TotalesCodigoPdfRenderer {

    @Override
    public void render(Integer anho, Integer mes, TotalesCodigoContent content, Path target) {
        Document document = new Document(PageSize.A4);
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            PdfHeaderFooter event = new PdfHeaderFooter(
                    PdfHeader.of("Totales de Código", mes + "/" + anho));
            writer.setPageEvent(event);
            document.open();
            event.setHeader(document);
            if (content.filas().isEmpty()) {
                document.add(new Paragraph("Sin datos para el período.",
                        new Font(Font.HELVETICA, 10, Font.BOLD)));
            } else {
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(95);
                table.setWidths(new float[]{10, 50, 15, 15, 15});
                table.addCell(PdfTables.noBorder("", PdfTables.bold(8), Element.ALIGN_LEFT));
                table.addCell(PdfTables.noBorder("Código", PdfTables.bold(8), Element.ALIGN_LEFT));
                table.addCell(PdfTables.noBorder("Novedades", PdfTables.bold(8), Element.ALIGN_CENTER));
                table.addCell(PdfTables.noBorder("Liquidado", PdfTables.bold(8), Element.ALIGN_CENTER));
                table.addCell(PdfTables.noBorder("Diferencia", PdfTables.bold(8), Element.ALIGN_CENTER));
                for (var fila : content.filas()) {
                    table.addCell(PdfTables.noBorder(String.valueOf(fila.codigoId()), PdfTables.font(8), Element.ALIGN_LEFT));
                    table.addCell(PdfTables.noBorder(fila.nombre(), PdfTables.font(8), Element.ALIGN_LEFT));
                    table.addCell(PdfTables.noBorder(fila.novedades(), PdfTables.font(8), Element.ALIGN_CENTER));
                    table.addCell(PdfTables.noBorder(fila.liquidado(), PdfTables.font(8), Element.ALIGN_CENTER));
                    table.addCell(PdfTables.noBorder(fila.diferencia(), PdfTables.font(8), Element.ALIGN_CENTER));
                }
                document.add(table);
            }
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar totales_codigo.pdf", e);
        }
    }
}
