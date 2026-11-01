package um.haberes.report.reports.comparacionremuneraciones.infrastructure.render;

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
import um.haberes.report.reports.comparacionremuneraciones.domain.model.ComparacionRemuneracionesContent;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.out.ComparacionRemuneracionesPdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class ComparacionRemuneracionesPdfRendererImpl implements ComparacionRemuneracionesPdfRenderer {

    private static final float[] WIDTHS = {8, 30, 20, 20, 20, 20, 20, 20, 20};

    @Override
    public void render(ComparacionRemuneracionesContent content, Path target) {
        Document document = new Document(PageSize.A4);
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            PdfHeader header = PdfHeader.of("Comparación de Remuneraciones",
                    content.mes() + "/" + content.anho(), List.of(),
                    "Actual: " + content.mes() + "/" + content.anho()
                            + "    Anterior: " + content.mesAnterior() + "/" + content.anhoAnterior());
            PdfHeaderFooter event = new PdfHeaderFooter(header);
            writer.setPageEvent(event);
            document.open();
            event.setHeader(document);

            if (content.dependencias().isEmpty()) {
                document.add(new Paragraph("Sin datos para el período.",
                        new Font(Font.HELVETICA, 10, Font.BOLD)));
            }
            for (ComparacionRemuneracionesContent.Dependencia dependencia : content.dependencias()) {
                Paragraph title = new Paragraph(text(dependencia.nombre()),
                        new Font(Font.HELVETICA, 12, Font.BOLD));
                title.setSpacingBefore(10);
                title.setSpacingAfter(5);
                document.add(title);

                PdfPTable table = new PdfPTable(9);
                table.setWidthPercentage(95);
                table.setWidths(WIDTHS);
                addHeader(table);
                for (ComparacionRemuneracionesContent.Fila fila : dependencia.filas()) {
                    for (String value : List.of(fila.legajoId(), fila.apellidoNombre(),
                            fila.brutoAnterior(), fila.brutoActual(), fila.brutoDiferencia(),
                            fila.brutoPorcentaje(), fila.netoAnterior(), fila.netoActual(),
                            fila.netoDiferencia())) {
                        table.addCell(PdfTables.noBorder(text(value), PdfTables.font(8), Element.ALIGN_LEFT));
                    }
                }
                document.add(table);
            }
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar comparacion-remuneraciones.pdf", e);
        }
    }

    private void addHeader(PdfPTable table) {
        PdfPCell emptyCell = new PdfPCell(new Phrase(""));
        emptyCell.setColspan(2);
        emptyCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(emptyCell);

        PdfPCell brutoCell = new PdfPCell(new Phrase("Bruto", PdfTables.bold(8)));
        brutoCell.setColspan(4);
        brutoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        brutoCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(brutoCell);

        PdfPCell netoCell = new PdfPCell(new Phrase("Neto", PdfTables.bold(8)));
        netoCell.setColspan(3);
        netoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        netoCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(netoCell);

        for (String title : List.of("Leg", "Apellido, Nombre", "Anterior", "Actual", "Dif", "",
                "Anterior", "Actual", "Dif")) {
            table.addCell(PdfTables.noBorder(title, PdfTables.bold(8), Element.ALIGN_LEFT));
        }
    }

    private String text(String value) {
        return value == null ? "" : value;
    }
}
