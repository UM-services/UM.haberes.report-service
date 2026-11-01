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
import um.haberes.report.reports.bono.domain.ports.out.BonoPdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class BonoPdfRendererImpl implements BonoPdfRenderer {

    private static final Font F8 = new Font(Font.HELVETICA, 8);
    private static final Font F8B = new Font(Font.HELVETICA, 8, Font.BOLD);
    private static final Font F9B = new Font(Font.HELVETICA, 9, Font.BOLD);
    private static final Font F10 = new Font(Font.HELVETICA, 10);
    private static final Font F10B = new Font(Font.HELVETICA, 10, Font.BOLD);
    private static final Font F11B = new Font(Font.HELVETICA, 11, Font.BOLD);
    private static final Font F12B = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font F16B = new Font(Font.HELVETICA, 16, Font.BOLD);

    @Override
    public void render(BonoContent content, Path target) {
        Document document = new Document(new Rectangle(PageSize.A4));
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter.getInstance(document, output);
            document.setMargins(20, 20, 20, 20);
            document.open();

            Image marca = Image.getInstance("marca_um.png");
            Image firma = Image.getInstance("firma_new.png");

            PdfPTable header = new PdfPTable(new float[]{2, 8});
            header.setWidthPercentage(100);
            header.addCell(image(marca));
            Paragraph paragraph = new Paragraph(new Phrase("Universidad de Mendoza", F16B));
            paragraph.add(new Phrase("\nBoulogne Sur Mer 683 - 5500 - Mendoza", F8));
            paragraph.add(new Phrase("\nRepública Argentina", F8));
            paragraph.add(new Phrase("\nCUIT: 30-51859446-6", F8));
            PdfPCell headerCell = new PdfPCell(paragraph);
            headerCell.setBorder(Rectangle.NO_BORDER);
            headerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerCell.setLeading(0, 1.5f);
            header.addCell(headerCell);
            document.add(header);

            centered(document, "Recibo por Pago de Remuneraciones", F12B);

            paragraph = new Paragraph(new Phrase("Apellido, Nombre: ", F8));
            paragraph.add(new Phrase(content.apellidoNombre(), F9B));
            paragraph.add(new Phrase("                   DU: ", F8));
            paragraph.add(new Phrase(text(content.documento()), F9B));
            paragraph.add(new Phrase("                   CUIL: ", F8));
            paragraph.add(new Phrase(text(content.cuil()), F9B));
            centered(document, paragraph);

            paragraph = new Paragraph(new Phrase("Legajo: ", F8));
            paragraph.add(new Phrase(text(content.legajoId()), F9B));
            paragraph.add(new Phrase("         Dependencia: ", F8));
            paragraph.add(new Phrase(text(content.dependencia()), F9B));
            paragraph.add(new Phrase("         Sueldo Básico: ", F8));
            paragraph.add(new Phrase(text(content.basico()), F9B));
            paragraph.add(new Phrase("         Ingreso: ", F8));
            paragraph.add(new Phrase(text(content.ingreso()), F9B));
            paragraph.add(new Phrase("         Antigüedad: ", F8));
            paragraph.add(new Phrase(text(content.antiguedad()), F9B));
            centered(document, paragraph);

            paragraph = new Paragraph(new Phrase("CBU: ", F8));
            paragraph.add(new Phrase(text(content.cbu()), F9B));
            paragraph.add(new Phrase("              Aporte Jubilatorio: ", F8));
            paragraph.add(new Phrase(text(content.aporteJubilatorio()), F8));
            paragraph.add(new Phrase("              Fecha Pago: ", F8));
            paragraph.add(new Phrase(text(content.fechaPago()), F9B));
            centered(document, paragraph);

            paragraph = new Paragraph(new Phrase("              Período Liquidado: ", F10));
            paragraph.add(new Phrase(text(content.fechaDesde()), F11B));
            paragraph.add(new Phrase("-", F11B));
            paragraph.add(new Phrase(text(content.fechaHasta()), F11B));
            paragraph.add(new Phrase("              Fecha Depósito: ", F8));
            paragraph.add(new Phrase(text(content.fechaDeposito()), F9B));
            centered(document, paragraph);

            cursos(document, content.cursos());
            cargos(document, "Actividad Docente", concat(content.docentes(),
                    content.adicionales()), false);
            cargos(document, "Actividad No Docente", content.noDocentes(), false);
            clases(document, content.clases());
            conceptos(document, content);
            firma(document, firma, content.neto(), content.cadena());
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar bono.pdf", e);
        }
    }

    private List<BonoContent.Cargo> concat(List<BonoContent.Cargo> first,
                                           List<BonoContent.Cargo> second) {
        List<BonoContent.Cargo> all = new java.util.ArrayList<>(first);
        all.addAll(second);
        return all;
    }

    private void cursos(Document document, List<BonoContent.Curso> cursos)
            throws DocumentException {
        if (cursos.isEmpty()) {
            return;
        }
        PdfPTable table = new PdfPTable(new float[]{4, 24, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f});
        table.setWidthPercentage(90);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        header(table, "Cargo", Element.ALIGN_RIGHT, "Curso", Element.ALIGN_LEFT,
                "Ds", Element.ALIGN_CENTER, "An", Element.ALIGN_CENTER,
                "S1", Element.ALIGN_CENTER, "S2", Element.ALIGN_CENTER,
                "Hr", Element.ALIGN_CENTER);
        for (BonoContent.Curso curso : cursos) {
            row(table, curso.cargo(), Element.ALIGN_RIGHT, true);
            row(table, curso.curso(), Element.ALIGN_LEFT, false);
            row(table, curso.desarraigo(), Element.ALIGN_CENTER, true);
            row(table, curso.anual(), Element.ALIGN_CENTER, true);
            row(table, curso.semestre1(), Element.ALIGN_CENTER, true);
            row(table, curso.semestre2(), Element.ALIGN_CENTER, true);
            row(table, curso.horas(), Element.ALIGN_CENTER, true);
        }
        document.add(table);
    }

    private void cargos(Document document, String title, List<BonoContent.Cargo> cargos,
                        boolean dummy) throws DocumentException {
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
            detalle.addCell(noBorder(cargo.dependencia(), Element.ALIGN_LEFT));
            detalle.addCell(noBorder(cargo.cargo(), Element.ALIGN_LEFT));
            detalle.addCell(noBorder(cargo.basico(), Element.ALIGN_RIGHT));
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
            table.addCell(noBorder(clase.dependencia(), Element.ALIGN_LEFT));
            table.addCell(noBorder(clase.cargo(), Element.ALIGN_LEFT));
            table.addCell(noBorder(clase.basico(), Element.ALIGN_RIGHT));
        }
        document.add(table);
    }

    private void conceptos(Document document, BonoContent content) throws DocumentException {
        PdfPTable grupo = new PdfPTable(new float[]{1, 1, 1});
        grupo.setWidthPercentage(100);
        grupo.setHorizontalAlignment(Element.ALIGN_CENTER);
        grupo.addCell(conceptoTable("Remun", content.remunerativos()));
        grupo.addCell(conceptoTable("No Rem", content.noRemunerativos()));
        grupo.addCell(conceptoTable("Deduc", content.deducciones()));
        document.add(grupo);

        PdfPTable totales = new PdfPTable(new float[]{1, 1, 1});
        totales.setWidthPercentage(100);
        totales.setHorizontalAlignment(Element.ALIGN_CENTER);
        totales.addCell(totalTable("Remunerativos", content.totalRemunerativo()));
        totales.addCell(totalTable("No Remunerativos", content.totalNoRemunerativo()));
        totales.addCell(totalTable("Deducciones", content.totalDeduccion()));
        document.add(totales);
    }

    private PdfPCell conceptoTable(String titulo, List<BonoContent.Concepto> conceptos) {
        PdfPTable table = new PdfPTable(new float[]{2, 10, 5});
        table.setWidthPercentage(100);
        table.addCell(bottom("Cód", Element.ALIGN_RIGHT));
        table.addCell(bottom("Concepto", Element.ALIGN_LEFT));
        table.addCell(bottom(titulo, Element.ALIGN_RIGHT));
        if (conceptos.isEmpty()) {
            table.addCell(noBorder(" ", Element.ALIGN_RIGHT));
            table.addCell(noBorder(" ", Element.ALIGN_LEFT));
            table.addCell(noBorder(" ", Element.ALIGN_RIGHT));
        }
        for (BonoContent.Concepto concepto : conceptos) {
            table.addCell(noBorder(concepto.codigo(), Element.ALIGN_RIGHT));
            table.addCell(noBorder(concepto.nombre(), Element.ALIGN_LEFT));
            table.addCell(noBorder(concepto.importe(), Element.ALIGN_RIGHT));
        }
        PdfPCell cell = new PdfPCell(table);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell totalTable(String etiqueta, String valor) {
        PdfPTable table = new PdfPTable(new float[]{2, 10, 5});
        table.setWidthPercentage(100);
        table.addCell(noBorderBold("", Element.ALIGN_RIGHT));
        table.addCell(noBorderBold(etiqueta, Element.ALIGN_RIGHT));
        table.addCell(noBorderBold(valor, Element.ALIGN_RIGHT));
        PdfPCell cell = new PdfPCell(table);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private void firma(Document document, Image firma, String neto, String cadena)
            throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{2, 8});
        table.setWidthPercentage(100);
        table.addCell(image(firma));
        Paragraph paragraph = new Paragraph(new Phrase("Neto: ", F10));
        paragraph.add(new Phrase(text(neto), F10B));
        paragraph.add(new Phrase("\nson pesos: ", F10));
        paragraph.add(new Phrase(text(cadena), F10B));
        PdfPCell cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cell.setLeading(0, 2f);
        table.addCell(cell);
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

    private void header(PdfPTable table, Object... values) {
        for (int index = 0; index < values.length; index += 2) {
            table.addCell(bottom((String) values[index], (Integer) values[index + 1]));
        }
    }

    private void row(PdfPTable table, String value, int alignment, boolean bold) {
        table.addCell(noBorder(value, alignment, bold));
    }

    private PdfPCell image(Image image) {
        PdfPCell cell = new PdfPCell(image);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell bottom(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, F8B));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private PdfPCell noBorder(String text, int alignment) {
        return noBorder(text, alignment, false);
    }

    private PdfPCell noBorder(String text, int alignment, boolean bold) {
        PdfPCell cell = new PdfPCell(new Phrase(text(text), bold ? F8B : F8));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private PdfPCell noBorderBold(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text(text), F8B));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
