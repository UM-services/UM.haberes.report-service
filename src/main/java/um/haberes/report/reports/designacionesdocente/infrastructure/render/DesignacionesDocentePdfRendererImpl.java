package um.haberes.report.reports.designacionesdocente.infrastructure.render;

import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.pdf.PdfHeader;
import um.haberes.report.infrastructure.render.pdf.PdfHeaderFooter;
import um.haberes.report.infrastructure.render.pdf.PdfTables;
import um.haberes.report.reports.designacionesdocente.domain.model.DesignacionesDocenteContent;
import um.haberes.report.reports.designacionesdocente.domain.ports.out.DesignacionesDocentePdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class DesignacionesDocentePdfRendererImpl implements DesignacionesDocentePdfRenderer {

    private static final float[] WIDTHS = {30, 17, 43, 10, 15, 15, 10, 12, 10, 10};

    @Override
    public void render(DesignacionesDocenteContent content, Path target) {
        Document document = new Document(PageSize.A4);
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            PdfHeaderFooter event = new PdfHeaderFooter(
                    PdfHeader.of("Designaciones por Docentes", content.mes() + "/" + content.anho()));
            writer.setPageEvent(event);
            document.open();
            event.setHeader(document);

            if (content.docentes().isEmpty()) {
                document.add(new Paragraph("Sin datos para el período.",
                        new Font(Font.HELVETICA, 10, Font.BOLD)));
            }
            for (DesignacionesDocenteContent.Docente docente : content.docentes()) {
                Paragraph title = new Paragraph();
                title.add(new Phrase(text(docente.legajoId()) + ". ", PdfTables.bold(9)));
                title.add(new Phrase(text(docente.apellidoNombre()), PdfTables.bold(9)));
                title.setSpacingBefore(10);
                title.setSpacingAfter(5);
                document.add(title);

                PdfPTable table = new PdfPTable(10);
                table.setWidthPercentage(95);
                table.setWidths(WIDTHS);
                addHeader(table);
                for (DesignacionesDocenteContent.Fila fila : docente.filas()) {
                    for (String value : List.of(fila.facultad(), fila.sede(), fila.espacio(),
                            fila.horas(), fila.cargo(), fila.designacion(), fila.horasDesignacion(),
                            fila.dictado(), "", "")) {
                        table.addCell(PdfTables.noBorder(text(value), PdfTables.font(8),
                                Element.ALIGN_LEFT));
                    }
                }
                document.add(table);
                document.add(Chunk.NEWLINE);
            }
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar designaciones-docente.pdf", e);
        }
    }

    private void addHeader(PdfPTable table) {
        for (String title : List.of("Facultad", "Sede", "Espacio", "H/Sem", "Cargo", "Designac.",
                "H/Des", "Dictado", "H/Eqv", "")) {
            table.addCell(PdfTables.noBorder(title, PdfTables.bold(8), Element.ALIGN_LEFT));
        }
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
