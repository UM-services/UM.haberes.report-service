package um.haberes.report.reports.detallecodigo.infrastructure.render;

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
import um.haberes.report.reports.detallecodigo.domain.model.DetalleCodigoContent;
import um.haberes.report.reports.detallecodigo.domain.ports.out.DetalleCodigoPdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class DetalleCodigoPdfRendererImpl implements DetalleCodigoPdfRenderer {

    private static final float[] WIDTHS = {10, 40, 16, 18, 10};
    private static final List<String> HEADERS =
            List.of("Legajo", "Apellido, Nombre", "Liquidado", "Novedad", "Cantidad");

    @Override
    public void render(DetalleCodigoContent content, Path target) {
        Document document = new Document(PageSize.A4);
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            PdfHeaderFooter event = new PdfHeaderFooter(PdfHeader.of(title(content),
                    content.mes() + "/" + content.anho()));
            writer.setPageEvent(event);
            document.open();
            event.setHeader(document);

            PdfPTable table = new PdfPTable(HEADERS.size());
            table.setWidthPercentage(95);
            table.setWidths(WIDTHS);
            for (String title : HEADERS) {
                table.addCell(PdfTables.noBorder(title, PdfTables.bold(8), Element.ALIGN_LEFT));
            }
            if (content.filas().isEmpty()) {
                document.add(new Paragraph("Sin datos para el período.",
                        new Font(Font.HELVETICA, 10, Font.BOLD)));
            }
            for (DetalleCodigoContent.Fila fila : content.filas()) {
                table.addCell(PdfTables.noBorder(text(fila.legajoId()), PdfTables.font(8),
                        Element.ALIGN_LEFT));
                table.addCell(PdfTables.noBorder(text(fila.apellidoNombre()), PdfTables.font(8),
                        Element.ALIGN_LEFT));
                table.addCell(PdfTables.noBorder(text(fila.liquidado()), PdfTables.font(8),
                        Element.ALIGN_RIGHT));
                table.addCell(PdfTables.noBorder(text(fila.novedad()), PdfTables.font(8),
                        Element.ALIGN_RIGHT));
                table.addCell(PdfTables.noBorder(text(fila.cantidad()), PdfTables.font(8),
                        Element.ALIGN_RIGHT));
            }
            document.add(table);
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar detallecodigo.pdf", e);
        }
    }

    private String title(DetalleCodigoContent content) {
        String nombre = content.codigoNombre();
        return nombre == null || nombre.isBlank()
                ? "Detalle por Código " + content.codigoId()
                : "Detalle por Código " + content.codigoId() + " · " + nombre;
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
