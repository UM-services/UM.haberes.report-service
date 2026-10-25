package um.haberes.report.reports.librosueldos.infrastructure.render;

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
import um.haberes.report.reports.librosueldos.domain.model.LibroSueldosContent;
import um.haberes.report.reports.librosueldos.domain.ports.out.LibroSueldosPdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class LibroSueldosPdfRendererImpl implements LibroSueldosPdfRenderer {

    private static final float[] WIDTHS = {7, 22, 12, 18, 14, 14, 13, 13};
    private static final List<String> HEADERS = List.of("Legajo", "Apellido, Nombre", "CUIL",
            "Dependencia", "Remunerativo", "No remunerativo", "Deducciones", "Neto");

    @Override
    public void render(LibroSueldosContent content, Path target) {
        Document document = new Document(PageSize.A4.rotate());
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            PdfHeaderFooter event = new PdfHeaderFooter(
                    PdfHeader.of("Libro de Sueldos", content.mes() + "/" + content.anho()));
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
                document.add(new Paragraph("Sin datos para el período.",
                        new Font(Font.HELVETICA, 10, Font.BOLD)));
            }
            for (LibroSueldosContent.Fila fila : content.filas()) {
                table.addCell(PdfTables.noBorder(text(fila.legajoId()), PdfTables.font(7),
                        Element.ALIGN_RIGHT));
                table.addCell(PdfTables.noBorder(text(fila.apellidoNombre()), PdfTables.font(7),
                        Element.ALIGN_LEFT));
                table.addCell(PdfTables.noBorder(text(fila.cuil()), PdfTables.font(7),
                        Element.ALIGN_LEFT));
                table.addCell(PdfTables.noBorder(text(fila.dependencia()), PdfTables.font(7),
                        Element.ALIGN_LEFT));
                table.addCell(PdfTables.noBorder(text(fila.remunerativo()), PdfTables.font(7),
                        Element.ALIGN_RIGHT));
                table.addCell(PdfTables.noBorder(text(fila.noRemunerativo()), PdfTables.font(7),
                        Element.ALIGN_RIGHT));
                table.addCell(PdfTables.noBorder(text(fila.deducciones()), PdfTables.font(7),
                        Element.ALIGN_RIGHT));
                table.addCell(PdfTables.noBorder(text(fila.neto()), PdfTables.font(7),
                        Element.ALIGN_RIGHT));
            }
            document.add(table);
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar librosueldos.pdf", e);
        }
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
