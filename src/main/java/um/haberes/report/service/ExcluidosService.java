package um.haberes.report.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import um.haberes.report.client.haberes.core.ExcluidoClient;
import um.haberes.report.client.haberes.core.LiquidacionClient;
import um.haberes.report.client.haberes.core.PersonaClient;
import um.haberes.report.kotlin.dto.haberes.core.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExcluidosService {
    private Environment environment;
    private ExcluidoClient excluidoClient;
    private LiquidacionClient liquidacionClient;
    private PersonaClient personaClient;

    public ExcluidosService(Environment environment, ExcluidoClient excluidoClient, LiquidacionClient liquidacionClient, PersonaClient personaClient) {
        this.environment = environment;
        this.excluidoClient = excluidoClient;
        this.liquidacionClient = liquidacionClient;
        this.personaClient = personaClient;
    }

    public String generate(Integer anho, Integer mes) {
        String path = environment.getProperty("path.files");
        String filename = path + "excluidos." + anho + "." + mes + ".pdf";

        generateReport(filename, anho, mes);
        return filename;
    }

    private void generateReport(String filename, Integer anho, Integer mes) {
        log.debug("Generating report for anho {}, mes {}", anho, mes);
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            log.debug("writer created");
            ExcluidosService.HeaderFooter event = new ExcluidosService.HeaderFooter(anho, mes);
            log.debug("event created");
            writer.setPageEvent(event);
            log.debug("page event set");
            document.open();
            log.debug("document opened");

            // Encabezado del documento
            event.setHeader(document);
            log.debug("header set");
// Tabla principal
            float[] widths = {20, 70, 20}; // Legajo, Nombre y Apellido, Neto
            PdfPTable detailTable = new PdfPTable(widths);
            detailTable.setWidthPercentage(95);

            // Cabecera de la tabla
            addTableHeader(detailTable);

            // Datos
            List<ExcluidoDto> excluidos = excluidoClient.findAllByPeriodo(anho, mes);
            for (ExcluidoDto excluido : excluidos) {
                Long legajoId = excluido.getLegajoId();
                PersonaDto persona = null;
                LiquidacionDto liquidacion = null;

                try {
                    persona = personaClient.findByLegajoId(legajoId);
                } catch (Exception e) {
                    log.warn("No se encontró persona para legajo {}", legajoId);
                }

                try {
                    liquidacion = liquidacionClient.findByUnique(legajoId, anho, mes);
                } catch (Exception e) {
                    log.warn("No se encontró liquidación para legajo {}", legajoId);
                }

                if (persona != null && liquidacion != null) {
                    addExcluidosDetails(document, detailTable, excluido, liquidacion, persona);
                }
            }

            // Agregar la tabla al documento
            document.add(detailTable);

            document.close();
            log.debug("document closed");
        } catch (Exception e) {
            log.error("Error generating report {}", e.getMessage(), e);
        }
    }

    private void addTableHeader(PdfPTable table) {
        Font headerFont = new Font(Font.HELVETICA, 8, Font.BOLD);

        PdfPCell cell = new PdfPCell(new Phrase("Legajo", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Nombre y Apellido", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Neto", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

    }

    private void addExcluidosDetails(Document document, PdfPTable detailTable, ExcluidoDto excluido,LiquidacionDto liquidacion, PersonaDto persona) throws DocumentException {

        // Datos de la tabla
        // Datos de la tabla
        var paragraph = new Paragraph();
        paragraph.add(new Phrase(excluido.getLegajoId() + ". ", new Font(Font.HELVETICA, 8)));
        var cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(persona.getNombre() + " " + persona.getApellido(), new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(liquidacion.getTotalNeto().toString(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

    }

    static class HeaderFooter extends PdfPageEventHelper {
        private final Integer anho;
        private final Integer mes;
        private PdfPTable headerTable;

        public HeaderFooter(Integer anho, Integer mes) {
            this.anho = anho;
            this.mes = mes;
        }

        public void setHeader(Document document) throws DocumentException, IOException {
            float[] columnHeader = {1, 4}; // Proporciones de las columnas
            headerTable = new PdfPTable(columnHeader);
            headerTable.setWidthPercentage(100);

            // Imagen de la Universidad
            log.debug("Imagen de la Universidad");
            Image image = Image.getInstance("marca_um.png");
            PdfPCell imageCell = new PdfPCell(image);
            imageCell.setBorder(Rectangle.NO_BORDER);
            imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerTable.addCell(imageCell);

            // Texto del encabezado
            log.debug("Texto del encabezado");

            // Párrafo para "UNIVERSIDAD DE MENDOZA" y el periodo
            Paragraph paragraph = new Paragraph();
            paragraph.add(new Phrase("UNIVERSIDAD DE MENDOZA", new Font(Font.HELVETICA, 15, Font.BOLD)));
            paragraph.add(new Phrase("\nPeriodo: ", new Font(Font.HELVETICA, 11)));
            paragraph.add(new Phrase(mes + "/" + anho, new Font(Font.HELVETICA, 11, Font.BOLD)));

            PdfPCell cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            headerTable.addCell(cell);

            // Nueva fila para titulo
            PdfPTable titleTable = new PdfPTable(1); // Solo una columna para centrar
            titleTable.setWidthPercentage(100);

            Paragraph titleParagraph = new Paragraph("Excluidos", new Font(Font.HELVETICA, 13, Font.BOLD));
            titleParagraph.setAlignment(Element.ALIGN_CENTER);

            PdfPCell titleCell = new PdfPCell(titleParagraph);
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleTable.addCell(titleCell);

            document.add(headerTable);
            document.add(Chunk.NEWLINE);
            document.add(titleTable); // Agregar título centrado
            document.add(Chunk.NEWLINE);
        }


        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable footer = new PdfPTable(1);
            try {
                footer.setTotalWidth(523);
                footer.setLockedWidth(true);
                footer.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                footer.addCell(new Phrase(String.format("Página %d", writer.getPageNumber()), new Font(Font.HELVETICA, 8, Font.BOLD)));
                footer.writeSelectedRows(0, -1, 36, 30, writer.getDirectContent());
            } catch (DocumentException e) {
                log.debug("Error al finalizar la página -> {}", e.getMessage());
            }
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            try {
                document.add(headerTable);
                document.add(Chunk.NEWLINE);
            } catch (DocumentException e) {
                log.debug("Error al iniciar la página -> {}", e.getMessage());
            }
        }
    }
}

