package um.haberes.report.reports.fusiondocente.infrastructure.render;

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
import um.haberes.report.reports.fusiondocente.domain.model.FusionDocenteContent;
import um.haberes.report.reports.fusiondocente.domain.ports.out.FusionDocentePdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Component
public class FusionDocentePdfRendererImpl implements FusionDocentePdfRenderer {

    private static final float[] WIDTHS = {10, 30, 45, 15, 15, 15};
    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(new Locale("en", "ES"));

    @Override
    public void render(FusionDocenteContent content, Path target) {
        Document document = new Document(PageSize.A4);
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            PdfHeaderFooter event = new PdfHeaderFooter(
                    PdfHeader.of("Fusión por Docente", content.mes() + "/" + content.anho()));
            writer.setPageEvent(event);
            document.open();
            event.setHeader(document);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(95);
            table.setWidths(WIDTHS);
            addHeader(table);
            if (content.filas().isEmpty()) {
                document.add(new Paragraph("Sin datos para el período.",
                        new Font(Font.HELVETICA, 10, Font.BOLD)));
            }
            for (FusionDocenteContent.Fila fila : content.filas()) {
                for (String value : List.of(fila.legajoId(), fila.apellidoNombre(), fila.facultad(),
                        fila.sede(), fila.cargo(), fila.basico())) {
                    table.addCell(PdfTables.noBorder(text(value), PdfTables.font(8),
                            Element.ALIGN_LEFT));
                }
            }
            document.add(table);
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar fusion.docente.pdf", e);
        }
    }

    private void addHeader(PdfPTable table) {
        for (String title : List.of("Legajo", "Apellido, Nombre", "Facultad", "Sede", "Cargo",
                "Básico")) {
            table.addCell(PdfTables.noBorder(title, PdfTables.bold(8), Element.ALIGN_LEFT));
        }
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
