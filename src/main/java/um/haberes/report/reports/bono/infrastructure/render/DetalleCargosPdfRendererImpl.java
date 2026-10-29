package um.haberes.report.reports.bono.infrastructure.render;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.Image;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.bono.domain.model.BonoContent;
import um.haberes.report.reports.bono.domain.model.DetalleCargosContent;
import um.haberes.report.reports.bono.domain.ports.out.DetalleCargosPdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class DetalleCargosPdfRendererImpl implements DetalleCargosPdfRenderer {

    private static final Font F8 = new Font(Font.HELVETICA, 8);
    private static final Font F8B = new Font(Font.HELVETICA, 8, Font.BOLD);
    private static final Font F9B = new Font(Font.HELVETICA, 9, Font.BOLD);
    private static final Font F11B = new Font(Font.HELVETICA, 11, Font.BOLD);
    private static final Font F12B = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font F16B = new Font(Font.HELVETICA, 16, Font.BOLD);

    @Override
    public void render(DetalleCargosContent content, Path target) {
        Document document = new Document(new Rectangle(PageSize.A4));
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter.getInstance(document, output);
            document.setMargins(20, 20, 20, 20);
            document.open();

            Image marca = Image.getInstance("marca_um.png");
            PdfPTable header = new PdfPTable(new float[]{2, 8});
            header.setWidthPercentage(100);
            PdfPCell imageCell = new PdfPCell(marca);
            imageCell.setBorder(Rectangle.NO_BORDER);
            header.addCell(imageCell);
            Paragraph paragraph = new Paragraph(new Phrase("Universidad de Mendoza", F16B));
            paragraph.add(new Phrase("\n" + text(content.facultad()), F11B));
            PdfPCell headerCell = new PdfPCell(paragraph);
            headerCell.setBorder(Rectangle.NO_BORDER);
            headerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerCell.setLeading(0, 1.5f);
            header.addCell(headerCell);
            document.add(header);

            centered(document, "Detalle de Cargos", F12B);

            paragraph = new Paragraph(new Phrase("Apellido, Nombre: ", F8));
            paragraph.add(new Phrase(text(content.apellidoNombre()), F9B));
            centered(document, paragraph);

            paragraph = new Paragraph(new Phrase("Legajo: ", F8));
            paragraph.add(new Phrase(text(content.legajoId()), F9B));
            paragraph.add(new Phrase("         Antigüedad: ", F8));
            paragraph.add(new Phrase(text(content.antiguedad()), F9B));
            centered(document, paragraph);

            cursos(document, content.cursos());
            cargos(document, "Actividad Docente", content.docentes());
            cargos(document, "Actividad No Docente", content.noDocentes());
            clases(document, content.clases());
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar el detalle de cargos", e);
        }
    }

    private void cursos(Document document, List<BonoContent.Curso> cursos)
            throws DocumentException {
        if (cursos.isEmpty()) {
            return;
        }
        PdfPTable table = new PdfPTable(new float[]{4, 24, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
        table.setWidthPercentage(90);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(bottom("Cargo", Element.ALIGN_RIGHT));
        table.addCell(bottom("Curso", Element.ALIGN_LEFT));
        table.addCell(bottom("Ds", Element.ALIGN_CENTER));
        table.addCell(bottom("An", Element.ALIGN_CENTER));
        table.addCell(bottom("S1", Element.ALIGN_CENTER));
        table.addCell(bottom("S2", Element.ALIGN_CENTER));
        table.addCell(bottom("Hr", Element.ALIGN_CENTER));
        for (BonoContent.Curso curso : cursos) {
            table.addCell(noBorder(curso.cargo(), Element.ALIGN_RIGHT, true));
            table.addCell(noBorder(curso.curso(), Element.ALIGN_LEFT, false));
            table.addCell(noBorder(curso.desarraigo(), Element.ALIGN_CENTER, true));
            table.addCell(noBorder(curso.anual(), Element.ALIGN_CENTER, true));
            table.addCell(noBorder(curso.semestre1(), Element.ALIGN_CENTER, true));
            table.addCell(noBorder(curso.semestre2(), Element.ALIGN_CENTER, true));
            table.addCell(noBorder(curso.horas(), Element.ALIGN_CENTER, true));
        }
        document.add(table);
    }

    private void cargos(Document document, String title, List<BonoContent.Cargo> cargos)
            throws DocumentException {
        if (cargos.isEmpty()) {
            return;
        }
        centered(document, title, F8B);
        PdfPTable grupo = new PdfPTable(new float[]{1, 1});
        grupo.setWidthPercentage(90);
        grupo.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPTable cabecera = new PdfPTable(new float[]{2, 6, 3});
        cabecera.setWidthPercentage(100);
        cabecera.addCell(bottom("Dep", Element.ALIGN_LEFT));
        cabecera.addCell(bottom("Cargo", Element.ALIGN_LEFT));
        cabecera.addCell(bottom("Básico", Element.ALIGN_RIGHT));
        PdfPCell headerCell = new PdfPCell(cabecera);
        headerCell.setBorder(Rectangle.NO_BORDER);
        grupo.addCell(headerCell);
        document.add(grupo);

        grupo = new PdfPTable(new float[]{1, 1});
        grupo.setWidthPercentage(90);
        grupo.setHorizontalAlignment(Element.ALIGN_CENTER);
        int count = 0;
        for (BonoContent.Cargo cargo : cargos) {
            count++;
            PdfPTable detalle = new PdfPTable(new float[]{2, 6, 3});
            detalle.setWidthPercentage(100);
            detalle.addCell(noBorder(cargo.dependencia(), Element.ALIGN_LEFT, false));
            detalle.addCell(noBorder(cargo.cargo(), Element.ALIGN_LEFT, false));
            detalle.addCell(noBorder(cargo.basico(), Element.ALIGN_RIGHT, false));
            PdfPCell cell = new PdfPCell(detalle);
            cell.setBorder(Rectangle.NO_BORDER);
            grupo.addCell(cell);
        }
        if (count % 2 != 0) {
            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            grupo.addCell(cell);
        }
        document.add(grupo);
    }

    private void clases(Document document, List<BonoContent.Cargo> clases)
            throws DocumentException {
        if (clases.isEmpty()) {
            return;
        }
        centered(document, "Actividad Académica", F8B);
        PdfPTable table = new PdfPTable(new float[]{2, 14, 3});
        table.setWidthPercentage(90);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(bottom("Dep", Element.ALIGN_LEFT));
        table.addCell(bottom("Cargo", Element.ALIGN_LEFT));
        table.addCell(bottom("Básico", Element.ALIGN_RIGHT));
        for (BonoContent.Cargo clase : clases) {
            table.addCell(noBorder(clase.dependencia(), Element.ALIGN_LEFT, false));
            table.addCell(noBorder(clase.cargo(), Element.ALIGN_LEFT, false));
            table.addCell(noBorder(clase.basico(), Element.ALIGN_RIGHT, false));
        }
        document.add(table);
    }

    private void centered(Document document, String texto, Font font) throws DocumentException {
        centered(document, new Paragraph(texto, font));
    }

    private void centered(Document document, Paragraph paragraph) throws DocumentException {
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setMultipliedLeading(2f);
        document.add(paragraph);
    }

    private PdfPCell bottom(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, F8B));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private PdfPCell noBorder(String text, int alignment, boolean bold) {
        PdfPCell cell = new PdfPCell(new Paragraph(text(text), bold ? F8B : F8));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
