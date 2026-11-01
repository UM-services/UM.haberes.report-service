package um.haberes.report.reports.cursocargonovedad.infrastructure.render;

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
import um.haberes.report.reports.cursocargonovedad.domain.model.CursocargoNovedadContent;
import um.haberes.report.reports.cursocargonovedad.domain.ports.out.CursocargoNovedadPdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class CursocargoNovedadPdfRendererImpl implements CursocargoNovedadPdfRenderer {

    private static final Font TITULO = new Font(Font.HELVETICA, 13, Font.BOLD);
    private static final Font SUBTITULO = new Font(Font.HELVETICA, 9, Font.BOLD);
    private static final Font TEXTO = new Font(Font.HELVETICA, 7);
    private static final Font TEXTO_BOLD = new Font(Font.HELVETICA, 7, Font.BOLD);
    private static final Color FONDO_ENCABEZADO = new Color(232, 234, 237);

    @Override
    public void render(CursocargoNovedadContent content, Path target) {
        Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter.getInstance(document, output);
            document.open();

            Paragraph titulo = new Paragraph("NOVEDADES DE CURSOS", TITULO);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph subtitulo = new Paragraph("Universidad de Mendoza - Haberes", TEXTO);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitulo);

            Paragraph periodo = new Paragraph(
                    "Período " + content.mes() + "/" + content.anho()
                            + (content.soloPendientes() ? " - Sólo pendientes" : "")
                            + " - " + content.filas().size() + " registros", SUBTITULO);
            periodo.setAlignment(Element.ALIGN_CENTER);
            periodo.setSpacingBefore(4);
            periodo.setSpacingAfter(10);
            document.add(periodo);

            document.add(tabla(content.filas()));
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar el reporte de novedades de cursos", e);
        }
    }

    private PdfPTable tabla(List<CursocargoNovedadContent.Fila> filas) {
        PdfPTable tabla = new PdfPTable(11);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{22, 12, 7, 18, 20, 10, 6, 4, 8, 8, 18});
        for (String encabezado : List.of("Facultad", "Sede", "Legajo", "Docente", "Curso", "Cargo",
                "Horas", "Ds", "Tipo", "Estado", "Solicitud")) {
            PdfPCell celda = new PdfPCell(new Phrase(encabezado, TEXTO_BOLD));
            celda.setBackgroundColor(FONDO_ENCABEZADO);
            celda.setPadding(3);
            celda.setBorder(Rectangle.BOTTOM);
            tabla.addCell(celda);
        }
        for (CursocargoNovedadContent.Fila fila : filas) {
            agregarCelda(tabla, fila.facultad(), false);
            agregarCelda(tabla, fila.sede(), false);
            agregarCelda(tabla, fila.legajo(), false);
            agregarCelda(tabla, fila.docente(), false);
            agregarCelda(tabla, fila.curso(), false);
            agregarCelda(tabla, fila.cargo(), false);
            agregarCelda(tabla, fila.horas(), true);
            agregarCelda(tabla, fila.desarraigo(), true);
            agregarCelda(tabla, fila.tipo(), false);
            agregarCelda(tabla, fila.estado(), false);
            agregarCelda(tabla, fila.solicitud(), false);
        }
        if (filas.isEmpty()) {
            PdfPCell celda = new PdfPCell(new Phrase("Sin novedades para el período.", TEXTO));
            celda.setColspan(11);
            celda.setPadding(6);
            celda.setBorder(Rectangle.BOTTOM);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(celda);
        }
        return tabla;
    }

    private void agregarCelda(PdfPTable tabla, String valor, boolean centrado) {
        PdfPCell celda = new PdfPCell(new Phrase(valor == null ? "" : valor, TEXTO));
        celda.setPadding(2);
        celda.setBorder(Rectangle.BOTTOM);
        celda.setHorizontalAlignment(centrado ? Element.ALIGN_CENTER : Element.ALIGN_LEFT);
        tabla.addCell(celda);
    }
}
