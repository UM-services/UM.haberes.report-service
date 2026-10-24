package um.haberes.report.reports.docentessede.infrastructure.render;

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
import um.haberes.report.reports.docentessede.domain.model.DocentesSedeContent;
import um.haberes.report.reports.docentessede.domain.ports.out.DocentesSedePdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class DocentesSedePdfRendererImpl implements DocentesSedePdfRenderer {

    @Override
    public void render(DocentesSedeContent content, Path target) {
        Document document = new Document(PageSize.A4);
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            PdfHeader header = PdfHeader.inline("Universidad de Mendoza",
                    List.of(content.facultad(), "Docentes por Sede"),
                    content.mes() + "/" + content.anho(), "Sede: " + content.geografica());
            PdfHeaderFooter event = new PdfHeaderFooter(header);
            writer.setPageEvent(event);
            document.open();
            event.setHeader(document);

            if (content.cursos().isEmpty()) {
                document.add(new Paragraph("Sin datos para el período.",
                        new Font(Font.HELVETICA, 10, Font.BOLD)));
            }
            for (DocentesSedeContent.Curso curso : content.cursos()) {
                PdfPTable cursoTable = new PdfPTable(new float[]{8.5f, 1.5f});
                cursoTable.setWidthPercentage(100);
                Paragraph cursoTitle = new Paragraph();
                cursoTitle.add(new Phrase("Curso: ", PdfTables.font(9)));
                cursoTitle.add(new Phrase(text(curso.nombre()), PdfTables.bold(9)));
                cursoTable.addCell(PdfTables.paragraph(cursoTitle, Element.ALIGN_LEFT));
                cursoTable.addCell(PdfTables.noBorder(text(curso.periodo()), PdfTables.font(9),
                        Element.ALIGN_RIGHT));
                document.add(cursoTable);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(95);
                table.setWidths(new float[]{25, 110, 35, 20, 10});
                for (String title : List.of("Cargo", "Docente", "Designación", "Horas", "Des")) {
                    table.addCell(PdfTables.noBorder(title, PdfTables.bold(8),
                            "Horas".equals(title) || "Des".equals(title)
                                    ? Element.ALIGN_CENTER : Element.ALIGN_LEFT));
                }
                for (DocentesSedeContent.Fila fila : curso.filas()) {
                    table.addCell(PdfTables.noBorder(text(fila.cargo()), PdfTables.font(8),
                            Element.ALIGN_LEFT));
                    table.addCell(PdfTables.noBorder(text(fila.docente()), PdfTables.bold(8),
                            Element.ALIGN_LEFT));
                    table.addCell(PdfTables.noBorder(text(fila.designacion()), PdfTables.font(8),
                            Element.ALIGN_LEFT));
                    table.addCell(PdfTables.noBorder(text(fila.horas()), PdfTables.font(8),
                            Element.ALIGN_CENTER));
                    table.addCell(PdfTables.noBorder(text(fila.desarraigo()), PdfTables.bold(8),
                            Element.ALIGN_CENTER));
                }
                for (int index = 0; index < 5; index++) {
                    table.addCell(PdfTables.noBorder("", PdfTables.font(8), Element.ALIGN_CENTER));
                }
                document.add(table);
                document.add(Chunk.NEWLINE);
            }
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar docentesSede.pdf", e);
        }
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
