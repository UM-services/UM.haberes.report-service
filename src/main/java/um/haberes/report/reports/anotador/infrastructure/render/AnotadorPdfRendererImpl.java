package um.haberes.report.reports.anotador.infrastructure.render;

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
import um.haberes.report.reports.anotador.domain.model.AnotadorContent;
import um.haberes.report.reports.anotador.domain.ports.out.AnotadorPdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class AnotadorPdfRendererImpl implements AnotadorPdfRenderer {

    @Override
    public void render(AnotadorContent content, Path target) {
        Document document = new Document(PageSize.A4);
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            PdfHeader header = content.pendientes()
                    ? PdfHeader.inline("UNIVERSIDAD DE MENDOZA",
                            List.of("Anotador - Pendientes"), content.mes() + "/" + content.anho(),
                            null)
                    : new PdfHeader("", content.mes() + "/" + content.anho(),
                            List.of("Anotador - Autorizados / Rechazados"), "marca_um.png",
                            "Autorizados", null, "UNIVERSIDAD DE MENDOZA", true);
            PdfHeaderFooter event = new PdfHeaderFooter(header);
            writer.setPageEvent(event);
            document.open();
            event.setHeader(document);

            if (content.registros().isEmpty() && content.rechazados().isEmpty()) {
                document.add(new Paragraph("Sin datos para el período.",
                        new Font(Font.HELVETICA, 10, Font.BOLD)));
            }
            for (AnotadorContent.Registro registro : content.registros()) {
                addDetalle(document, registro);
                if (content.pendientes()) {
                    document.add(Chunk.NEWLINE);
                }
            }
            if (content.pendientes()) {
                document.close();
                return;
            }

            if (!content.rechazados().isEmpty()) {
                Paragraph titulo = new Paragraph("Rechazados",
                        new Font(Font.HELVETICA, 14, Font.BOLD));
                titulo.setAlignment(Element.ALIGN_CENTER);
                document.add(titulo);
                for (AnotadorContent.Registro registro : content.rechazados()) {
                    addDetalle(document, registro);
                }
            }
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar el reporte del anotador", e);
        }
    }

    private void addDetalle(Document document, AnotadorContent.Registro registro)
            throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(95);

        Paragraph paragraph = new Paragraph();
        paragraph.add(new Phrase("\nLegajo: ", new Font(Font.HELVETICA, 11)));
        paragraph.add(new Phrase(registro.legajoId() + " - " + registro.documento() + " - "
                + registro.apellidoNombre(), new Font(Font.HELVETICA, 11, Font.BOLD)));
        table.addCell(cell(paragraph));

        paragraph = new Paragraph();
        paragraph.add(new Phrase("\t\t" + registro.facultad(),
                new Font(Font.HELVETICA, 10, Font.BOLD)));
        table.addCell(cell(paragraph));

        paragraph = new Paragraph();
        paragraph.add(new Phrase("\t\t\t\t" + registro.fecha(), new Font(Font.HELVETICA, 9)));
        paragraph.add(new Phrase("\t\t" + registro.user(),
                new Font(Font.HELVETICA, 10, Font.ITALIC)));
        table.addCell(cell(paragraph));

        paragraph = new Paragraph();
        paragraph.add(new Phrase("\t\t\t\t\t\t" + registro.estado(),
                new Font(Font.HELVETICA, 10, Font.BOLD)));
        table.addCell(cell(paragraph));

        paragraph = new Paragraph();
        paragraph.add(new Phrase("\t\t\t\t" + registro.anotacion(),
                new Font(Font.HELVETICA, 9)));
        table.addCell(cell(paragraph));

        paragraph = new Paragraph();
        paragraph.add(new Phrase("\t\t\t\tRespuesta: ", new Font(Font.HELVETICA, 8, Font.BOLD)));
        paragraph.add(new Phrase("\n\t\t\t\t\t\t" + registro.respuesta(),
                new Font(Font.HELVETICA, 9)));
        table.addCell(cell(paragraph));

        document.add(table);
    }

    private PdfPCell cell(Paragraph paragraph) {
        PdfPCell cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
}
