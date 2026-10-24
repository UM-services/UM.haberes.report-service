package um.haberes.report.reports.designacionesdetalle.infrastructure.render;

import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.designacionesdetalle.domain.model.DesignacionesDetalleContent;
import um.haberes.report.reports.designacionesdetalle.domain.ports.out.DesignacionesDetallePdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class DesignacionesDetallePdfRendererImpl implements DesignacionesDetallePdfRenderer {

    private static final Font TITULO = new Font(Font.HELVETICA, 13, Font.BOLD);
    private static final Font SUBTITULO = new Font(Font.HELVETICA, 9, Font.BOLD);
    private static final Font TEXTO = new Font(Font.HELVETICA, 8);
    private static final Font TEXTO_BOLD = new Font(Font.HELVETICA, 8, Font.BOLD);
    private static final Color FONDO_ENCABEZADO = new Color(232, 234, 237);

    @Override
    public void render(DesignacionesDetalleContent content, Path target) {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter.getInstance(document, output);
            document.open();

            Paragraph titulo = new Paragraph("DESIGNACIONES", TITULO);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph subtitulo = new Paragraph("Universidad de Mendoza - Haberes", TEXTO);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitulo);

            Paragraph persona = new Paragraph(content.persona() + " - Período "
                    + content.mes() + "/" + content.anho(), TEXTO_BOLD);
            persona.setAlignment(Element.ALIGN_CENTER);
            persona.setSpacingBefore(6);
            persona.setSpacingAfter(12);
            document.add(persona);

            liquidadas(document, content);
            cursos(document, content);
            fusiones(document, content);
            resumen(document, content);
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar el detalle de designaciones", e);
        }
    }

    private void liquidadas(Document document, DesignacionesDetalleContent content)
            throws DocumentException {
        subtitulo(document, "Designaciones liquidadas - mes anterior ("
                + content.periodoAnterior().mes() + "/" + content.periodoAnterior().anho() + ")");
        PdfPTable tabla = tabla(new float[]{32, 20, 38, 14},
                "Facultad", "Sede", "Designación", "Importe");
        for (DesignacionesDetalleContent.LiquidaFila fila : content.liquidadas()) {
            agregarFila(tabla, fila.facultad(), fila.sede(), fila.designacion(), fila.importe());
        }
        if (content.liquidadas().isEmpty()) {
            agregarFilaVacia(tabla, 4);
        }
        document.add(tabla);
        total(document, "Total Designaciones", content.totalLiquidadas());
    }

    private void cursos(Document document, DesignacionesDetalleContent content)
            throws DocumentException {
        subtitulo(document, "Cursos asignados (" + content.mes() + "/" + content.anho() + ")");
        PdfPTable tabla = tabla(new float[]{22, 13, 29, 8, 10, 14, 12, 6, 6, 6, 6},
                "Facultad", "Sede", "Curso", "Horas", "Cargo", "Designación", "Importe",
                "Ds", "An", "S1", "S2");
        for (DesignacionesDetalleContent.CursoFila fila : content.cursos()) {
            agregarFila(tabla, fila.facultad(), fila.sede(), fila.curso(), fila.horas(),
                    fila.cargo(), fila.designacion(), fila.importe(), fila.desarraigo(),
                    fila.anual(), fila.semestre1(), fila.semestre2());
        }
        if (content.cursos().isEmpty()) {
            agregarFilaVacia(tabla, 11);
        }
        document.add(tabla);
        total(document, "Total Cursos", content.totalCursos());
    }

    private void fusiones(Document document, DesignacionesDetalleContent content)
            throws DocumentException {
        subtitulo(document, "Designaciones fusionadas (" + content.mes() + "/" + content.anho() + ")");
        PdfPTable tabla = tabla(new float[]{30, 14, 14, 18, 18, 12},
                "Facultad", "Sede", "Cargo", "Designación", "Categoría", "Importe");
        for (DesignacionesDetalleContent.FusionFila fila : content.fusiones()) {
            agregarFila(tabla, fila.facultad(), fila.sede(), fila.cargo(), fila.designacion(),
                    fila.categoria(), fila.importe());
        }
        if (content.fusiones().isEmpty()) {
            agregarFilaVacia(tabla, 6);
        }
        document.add(tabla);
        total(document, "Total Fusión", content.totalFusion());
    }

    private void resumen(Document document, DesignacionesDetalleContent content)
            throws DocumentException {
        Paragraph resumen = new Paragraph("Resumen del control", SUBTITULO);
        resumen.setSpacingBefore(14);
        resumen.setSpacingAfter(4);
        document.add(resumen);

        PdfPTable tabla = tabla(new float[]{25, 25, 25, 25},
                "Total Designaciones", "Total Cursos", "Total Fusión", "Dif. Designaciones");
        agregarFila(tabla, content.totalLiquidadas(), content.totalCursos(),
                content.totalFusion(), content.diferencia());
        document.add(tabla);

        document.add(Chunk.NEWLINE);
        Paragraph pie = new Paragraph(
                "Diferencia: novedad de código 27 registrada para el período.", TEXTO);
        pie.setSpacingBefore(6);
        document.add(pie);
    }

    private void subtitulo(Document document, String texto) throws DocumentException {
        Paragraph subtitulo = new Paragraph(texto, SUBTITULO);
        subtitulo.setSpacingBefore(10);
        subtitulo.setSpacingAfter(4);
        document.add(subtitulo);
    }

    private void total(Document document, String etiqueta, String valor) throws DocumentException {
        Paragraph parrafo = new Paragraph(etiqueta + ": " + valor, TEXTO_BOLD);
        parrafo.setAlignment(Element.ALIGN_RIGHT);
        parrafo.setSpacingBefore(3);
        parrafo.setSpacingAfter(6);
        document.add(parrafo);
    }

    private PdfPTable tabla(float[] anchos, String... encabezados) {
        PdfPTable tabla = new PdfPTable(encabezados.length);
        tabla.setWidthPercentage(100);
        tabla.setWidths(anchos);
        for (String encabezado : encabezados) {
            PdfPCell celda = new PdfPCell(new Phrase(encabezado, TEXTO_BOLD));
            celda.setBackgroundColor(FONDO_ENCABEZADO);
            celda.setPadding(3);
            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
            celda.setBorder(Rectangle.BOTTOM);
            tabla.addCell(celda);
        }
        return tabla;
    }

    private void agregarFila(PdfPTable tabla, String... valores) {
        for (String valor : valores) {
            PdfPCell celda = new PdfPCell(new Phrase(valor == null ? "" : valor, TEXTO));
            celda.setPadding(2);
            celda.setBorder(Rectangle.BOTTOM);
            celda.setHorizontalAlignment(esMonto(valor) ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
            tabla.addCell(celda);
        }
    }

    private void agregarFilaVacia(PdfPTable tabla, int columnas) {
        PdfPCell celda = new PdfPCell(new Phrase("Sin registros para el período.", TEXTO));
        celda.setColspan(columnas);
        celda.setPadding(4);
        celda.setBorder(Rectangle.BOTTOM);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(celda);
    }

    private boolean esMonto(String valor) {
        return valor != null && valor.matches("^-?\\d{1,3}(\\.\\d{3})*,\\d{2}$");
    }
}
