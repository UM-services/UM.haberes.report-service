package um.haberes.report.reports.novedadesdocentes.infrastructure.render;

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
import um.haberes.report.reports.novedadesdocentes.domain.model.NovedadesDocentesContent;
import um.haberes.report.reports.novedadesdocentes.domain.ports.out.NovedadesDocentesPdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class NovedadesDocentesPdfRendererImpl implements NovedadesDocentesPdfRenderer {

    @Override
    public void render(NovedadesDocentesContent content, Path target) {
        Document document = new Document(PageSize.A4);
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            List<String> lines = new ArrayList<>();
            if (content.facultad() != null) {
                lines.add(content.facultad());
            }
            lines.add("Novedades Docentes");
            PdfHeaderFooter event = new PdfHeaderFooter(PdfHeader.inline(
                    "UNIVERSIDAD DE MENDOZA", lines, content.mes() + "/" + content.anho(), null));
            writer.setPageEvent(event);
            document.open();
            event.setHeader(document);

            if (content.sedes().isEmpty()) {
                document.add(new Paragraph("Sin datos para el período.",
                        new Font(Font.HELVETICA, 10, Font.BOLD)));
            }
            for (NovedadesDocentesContent.Sede sede : content.sedes()) {
                PdfPTable sedeTable = new PdfPTable(1);
                sedeTable.setWidthPercentage(100);
                Paragraph sedeTitle = new Paragraph();
                sedeTitle.add(new Phrase("\nSede: ", PdfTables.font(11)));
                sedeTitle.add(new Phrase(text(sede.nombre()), PdfTables.bold(11)));
                sedeTable.addCell(PdfTables.paragraph(sedeTitle, Element.ALIGN_CENTER));
                document.add(sedeTable);

                for (NovedadesDocentesContent.Seccion seccion : sede.secciones()) {
                    PdfPTable sectionTable = new PdfPTable(1);
                    sectionTable.setWidthPercentage(70);
                    sectionTable.addCell(PdfTables.noBorder(seccion.titulo(), PdfTables.bold(11),
                            Element.ALIGN_LEFT));
                    document.add(sectionTable);
                    for (NovedadesDocentesContent.Novedad novedad : seccion.novedades()) {
                        addNovedad(document, novedad);
                    }
                }
            }
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar novedadesDocentes.pdf", e);
        }
    }

    private void addNovedad(Document document, NovedadesDocentesContent.Novedad novedad)
            throws DocumentException {
        PdfPTable cursoTable = new PdfPTable(1);
        cursoTable.setWidthPercentage(100);
        Paragraph curso = new Paragraph();
        curso.add(new Phrase("Curso: ", PdfTables.font(10)));
        curso.add(new Phrase(text(novedad.curso()), PdfTables.bold(10)));
        cursoTable.addCell(PdfTables.paragraph(curso, Element.ALIGN_LEFT));
        document.add(cursoTable);

        PdfPTable table = new PdfPTable(new float[]{25, 100, 15, 20, 20, 20});
        table.setWidthPercentage(95);
        for (String title : List.of("Cargo", "Docente", "Horas", "Desarraigo", "Aut.", "Rech.")) {
            table.addCell(PdfTables.noBorder(title, PdfTables.bold(8),
                    "Cargo".equals(title) || "Docente".equals(title)
                            ? Element.ALIGN_LEFT : Element.ALIGN_CENTER));
        }
        table.addCell(PdfTables.noBorder(text(novedad.cargo()), PdfTables.font(8), Element.ALIGN_LEFT));
        table.addCell(PdfTables.noBorder(text(novedad.docente()), PdfTables.bold(8), Element.ALIGN_LEFT));
        table.addCell(PdfTables.noBorder(text(novedad.horas()), PdfTables.font(8), Element.ALIGN_CENTER));
        table.addCell(PdfTables.noBorder(text(novedad.desarraigo()), PdfTables.bold(8), Element.ALIGN_CENTER));
        table.addCell(PdfTables.noBorder(text(novedad.autorizado()), PdfTables.bold(8), Element.ALIGN_CENTER));
        table.addCell(PdfTables.noBorder(text(novedad.rechazado()), PdfTables.bold(8), Element.ALIGN_CENTER));
        document.add(table);

        PdfPTable observaciones = new PdfPTable(1);
        observaciones.setWidthPercentage(85);
        Paragraph solicitud = new Paragraph();
        solicitud.add(new Phrase("Observaciones: ", PdfTables.bold(10)));
        solicitud.add(new Phrase(text(novedad.solicitud()), PdfTables.font(10)));
        observaciones.addCell(PdfTables.paragraph(solicitud, Element.ALIGN_LEFT));
        Paragraph respuesta = new Paragraph();
        respuesta.add(new Phrase("Respuesta: ", PdfTables.bold(10)));
        respuesta.add(new Phrase(text(novedad.respuesta()), PdfTables.font(10)));
        observaciones.addCell(PdfTables.paragraph(respuesta, Element.ALIGN_LEFT));
        document.add(observaciones);
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
