package um.haberes.report.infrastructure.render.pdf;

import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;

/** Constructores de celdas comunes, para no repetir bordes/alineaciones en cada renderer. */
public final class PdfTables {

    private PdfTables() {
    }

    public static PdfPCell noBorder(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    public static PdfPCell bottomBorder(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    public static PdfPCell paragraph(Paragraph paragraph, int alignment) {
        PdfPCell cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    public static Font font(int size) {
        return new Font(Font.HELVETICA, size);
    }

    public static Font bold(int size) {
        return new Font(Font.HELVETICA, size, Font.BOLD);
    }

    public static int center() {
        return Element.ALIGN_CENTER;
    }
}
