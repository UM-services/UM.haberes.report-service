package um.haberes.report.infrastructure.render.pdf;

import lombok.extern.slf4j.Slf4j;
import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.Image;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.pdf.PdfWriter;

import java.io.IOException;

/**
 * Encabezado y pie comunes. Se instala como page event y se invoca {@link #setHeader(Document)}
 * una vez abierto el documento, igual que el codigo legacy, para que la primera pagina lleve
 * el encabezado.
 */
@Slf4j
public class PdfHeaderFooter extends PdfPageEventHelper {

    private final PdfHeader header;
    private PdfPTable headerTable;
    private Paragraph stateParagraph;

    public PdfHeaderFooter(PdfHeader header) {
        this.header = header;
    }

    public void setHeader(Document document) throws DocumentException, IOException {
        if (headerTable == null) {
            headerTable = buildTable();
            stateParagraph = buildState();
        }
        document.add(headerTable);
        if (stateParagraph != null) {
            document.add(stateParagraph);
        }
        document.add(Chunk.NEWLINE);
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        // En la primera pagina el evento corre antes de setHeader: no hay tabla todavia.
        if (headerTable == null) {
            return;
        }
        try {
            document.add(headerTable);
            document.add(Chunk.NEWLINE);
        } catch (DocumentException e) {
            log.debug("Error al iniciar la pagina -> {}", e.getMessage());
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfPTable footer = new PdfPTable(1);
        try {
            footer.setTotalWidth(523);
            footer.setLockedWidth(true);
            footer.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            footer.addCell(new Phrase(String.format("Página %d", writer.getPageNumber()),
                    new Font(Font.HELVETICA, 8, Font.BOLD)));
            footer.writeSelectedRows(0, -1, 36, 30, writer.getDirectContent());
        } catch (DocumentException e) {
            log.debug("Error al finalizar la pagina -> {}", e.getMessage());
        }
    }

    private PdfPTable buildTable() throws IOException {
        PdfPTable table = new PdfPTable(new float[]{1, 4});
        table.setWidthPercentage(100);

        if (header.logoPath() != null) {
            Image image = Image.getInstance(header.logoPath());
            PdfPCell imageCell = new PdfPCell(image);
            imageCell.setBorder(Rectangle.NO_BORDER);
            imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(imageCell);
        } else {
            PdfPCell empty = new PdfPCell(new Phrase(""));
            empty.setBorder(Rectangle.NO_BORDER);
            table.addCell(empty);
        }

        Paragraph paragraph = new Paragraph();
        paragraph.add(new Phrase(header.institution(), new Font(Font.HELVETICA, 15, Font.BOLD)));
        if (header.linesBeforePeriod()) {
            for (String line : header.lines()) {
                paragraph.add(new Phrase("\n" + line, new Font(Font.HELVETICA, 11)));
            }
        }
        if (header.period() != null) {
            paragraph.add(new Phrase("\nPeriodo: ", new Font(Font.HELVETICA, 11)));
            paragraph.add(new Phrase(header.period(), new Font(Font.HELVETICA, 11, Font.BOLD)));
        }
        if (!header.linesBeforePeriod()) {
            for (String line : header.lines()) {
                paragraph.add(new Phrase("\n" + line, new Font(Font.HELVETICA, 11)));
            }
        }

        PdfPCell cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(cell);

        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);
        Paragraph title = new Paragraph(header.title(), new Font(Font.HELVETICA, 13, Font.BOLD));
        title.setAlignment(Element.ALIGN_CENTER);
        PdfPCell titleCell = new PdfPCell(title);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        titleTable.addCell(titleCell);

        if (header.subtitle() != null) {
            Paragraph subtitle = new Paragraph(header.subtitle(), new Font(Font.HELVETICA, 11));
            subtitle.setAlignment(Element.ALIGN_CENTER);
            PdfPCell subtitleCell = new PdfPCell(subtitle);
            subtitleCell.setBorder(Rectangle.NO_BORDER);
            subtitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            subtitleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleTable.addCell(subtitleCell);
        }

        PdfPTable container = new PdfPTable(1);
        container.setWidthPercentage(100);
        PdfPCell headerCell = new PdfPCell(table);
        headerCell.setBorder(Rectangle.NO_BORDER);
        container.addCell(headerCell);
        PdfPCell titleContainer = new PdfPCell(titleTable);
        titleContainer.setBorder(Rectangle.NO_BORDER);
        container.addCell(titleContainer);
        return container;
    }

    private Paragraph buildState() {
        if (header.state() == null) {
            return null;
        }
        Paragraph paragraph = new Paragraph(header.state(), new Font(Font.HELVETICA, 14, Font.BOLD));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        return paragraph;
    }
}
