package um.haberes.report.reports.cargosclaseperiodo.infrastructure.render;

import org.openpdf.text.Chunk;
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
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargosClasePeriodoContent;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargosClasePeriodoContent.Fila;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargosClasePeriodoContent.Sede;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.out.CargosClasePeriodoPdfRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class CargosClasePeriodoPdfRendererImpl implements CargosClasePeriodoPdfRenderer {

    private static final float[] WIDTHS = {10, 30, 35, 30, 10, 10, 10, 10};

    @Override
    public void render(CargosClasePeriodoContent content, Path target) {
        Document document = new Document(PageSize.A4);
        try (OutputStream output = Files.newOutputStream(target)) {
            PdfWriter writer = PdfWriter.getInstance(document, output);
            PdfHeader header = PdfHeader.of("Cargos con Clase por Periodo",
                    content.mes() + "/" + content.anho(),
                    content.facultad() == null ? List.of() : List.of(content.facultad()));
            PdfHeaderFooter event = new PdfHeaderFooter(header);
            writer.setPageEvent(event);
            document.open();
            event.setHeader(document);

            if (content.sedes().isEmpty()) {
                document.add(new Paragraph("Sin datos para el período.",
                        new Font(Font.HELVETICA, 10, Font.BOLD)));
            }
            for (Sede sede : content.sedes()) {
                Paragraph title = new Paragraph("Sede: " + text(sede.nombre()),
                        new Font(Font.HELVETICA, 12, Font.BOLD));
                title.setAlignment(Element.ALIGN_LEFT);
                document.add(title);
                document.add(Chunk.NEWLINE);

                PdfPTable table = new PdfPTable(8);
                table.setWidthPercentage(95);
                table.setWidths(WIDTHS);
                addHeader(table);
                for (Fila fila : sede.filas()) {
                    table.addCell(PdfTables.noBorder(text(fila.legajoId()), PdfTables.font(8), Element.ALIGN_LEFT));
                    table.addCell(PdfTables.noBorder(text(fila.apellidoNombre()), PdfTables.font(8), Element.ALIGN_LEFT));
                    table.addCell(PdfTables.noBorder(text(fila.cargo()), PdfTables.font(8), Element.ALIGN_LEFT));
                    table.addCell(PdfTables.noBorder(text(fila.descripcion()), PdfTables.font(8), Element.ALIGN_LEFT));
                    table.addCell(PdfTables.noBorder(text(fila.periodoDesde()), PdfTables.font(8), Element.ALIGN_LEFT));
                    table.addCell(PdfTables.noBorder(text(fila.periodoHasta()), PdfTables.font(8), Element.ALIGN_LEFT));
                    table.addCell(PdfTables.noBorder(text(fila.horas()) + " ", PdfTables.bold(8), Element.ALIGN_LEFT));
                    table.addCell(PdfTables.noBorder(text(fila.clase()), PdfTables.font(8), Element.ALIGN_LEFT));
                }
                document.add(table);
                document.add(Chunk.NEWLINE);
            }
            document.close();
        } catch (IOException | DocumentException e) {
            throw new ReportException("No se pudo generar cargos-clase-periodo.pdf", e);
        }
    }

    private void addHeader(PdfPTable table) {
        for (String title : List.of("Legajo", "Apellido, Nombre", "Cargo", "Descripción",
                "Desde", "Hasta", "Horas", "Clase")) {
            table.addCell(PdfTables.noBorder(title, PdfTables.bold(8), Element.ALIGN_LEFT));
        }
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
