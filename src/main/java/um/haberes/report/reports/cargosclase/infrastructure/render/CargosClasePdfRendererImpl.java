package um.haberes.report.reports.cargosclase.infrastructure.render;

import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.pdf.PdfHeader;
import um.haberes.report.infrastructure.render.pdf.PdfHeaderFooter;
import um.haberes.report.reports.cargosclase.domain.model.CargosClaseContent;
import um.haberes.report.reports.cargosclase.domain.model.CargosClaseContent.ClaseConCargos;
import um.haberes.report.reports.cargosclase.domain.ports.out.CargosClasePdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class CargosClasePdfRendererImpl implements CargosClasePdfRenderer {

    @Override
    public void render(CargosClaseContent content, Path target) {
        Document document = new Document(PageSize.A4);
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            PdfHeaderFooter event = new PdfHeaderFooter(PdfHeader.of("Cargos con Clase", null));
            writer.setPageEvent(event);
            document.open();
            event.setHeader(document);

            if (content.clases().isEmpty()) {
                document.add(new Paragraph("Sin datos para el período.",
                        new Font(Font.HELVETICA, 10, Font.BOLD)));
            }
            for (ClaseConCargos grupo : content.clases()) {
                document.add(new Paragraph("Clase " + grupo.clase().getClaseId()
                        + " (" + grupo.clase().getValorHora() + ")",
                        new Font(Font.HELVETICA, 12, Font.BOLD)));
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setSpacingBefore(5);
                table.addCell(cell("#", Element.ALIGN_LEFT));
                table.addCell(cell("Cargo", Element.ALIGN_LEFT));
                for (var cargo : grupo.cargos()) {
                    table.addCell(cell(String.valueOf(cargo.cargoClaseId()), Element.ALIGN_LEFT));
                    table.addCell(cell(cargo.nombre(), Element.ALIGN_LEFT));
                }
                document.add(table);
                document.add(Chunk.NEWLINE);
            }
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar cargos-clase.pdf", e);
        }
    }

    private org.openpdf.text.pdf.PdfPCell cell(String value, int alignment) {
        var cell = new org.openpdf.text.pdf.PdfPCell(
                new org.openpdf.text.Phrase(value == null ? "" : value, new Font(Font.HELVETICA, 8)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }
}
