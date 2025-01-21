package um.haberes.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import um.haberes.report.client.haberes.core.CodigoClient;
import um.haberes.report.client.haberes.core.TotalItemClient;
import um.haberes.report.client.haberes.core.TotalNovedadClient;
import um.haberes.report.kotlin.dto.haberes.core.CodigoDto;
import um.haberes.report.kotlin.dto.haberes.core.TotalItemDto;
import um.haberes.report.kotlin.dto.haberes.core.TotalNovedadDto;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TotalesCodigoService {

    private final Environment environment;
    private final CodigoClient codigoClient;
    private final TotalNovedadClient totalNovedadClient;
    private final TotalItemClient totalItemClient;

    public TotalesCodigoService(CodigoClient codigoClient, TotalNovedadClient totalNovedadClient, Environment environment, TotalItemClient totalItemClient) {
        this.environment = environment;
        this.codigoClient = codigoClient;
        this.totalNovedadClient = totalNovedadClient;
        this.totalItemClient = totalItemClient;
    }

    public String generate(Integer anho, Integer mes) {
        String path = environment.getProperty("path.files");
        String filename = path + "totales.codigo." + "." + anho + "." + mes + ".pdf";

        generateReport(filename, anho, mes);
        return filename;
    }

    private void generateReport(String filename, Integer anho, Integer mes) {
        log.debug("Generating report for anho {}, mes {}", anho, mes);
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            TotalesCodigoService.HeaderFooter event = new TotalesCodigoService.HeaderFooter(anho, mes);
            writer.setPageEvent(event);
            document.open();

            // Encabezado del documento
            log.debug("Setting header");
            event.setHeader(document);

            PdfPTable detailTable = new PdfPTable(5);
            float[] columnHeader = {10, 50, 15, 15, 15};
            detailTable.setWidthPercentage(95);
            detailTable.setWidths(columnHeader);

            // Agregar encabezado de la tabla
            addTableHeader(detailTable);

            // Obtener totales y códigos
            List<TotalNovedadDto> totalNovedadList = totalNovedadClient.findAllByPeriodo(anho, mes);
            if (totalNovedadList == null || totalNovedadList.isEmpty()) {
                log.warn("No se encontraron totales de novedades para el periodo {}-{}", anho, mes);
                return;
            }

            List<TotalItemDto> totalItemList = totalItemClient.findAllByPeriodo(anho, mes);
            if (totalItemList == null || totalItemList.isEmpty()) {
                log.warn("No se encontraron totales de items para el periodo {}-{}", anho, mes);
                return;
            }

            List<CodigoDto> codigos = codigoClient.findAllByPeriodo(anho, mes);
            if (codigos == null || codigos.isEmpty()) {
                log.warn("No se encontraron códigos para el periodo {}-{}", anho, mes);
                return;
            }

            // Ordenar los códigos por ID de manera ascendente
            codigos = codigos.stream()
                    .filter(Objects::nonNull) // Asegurarse de no tener valores nulos
                    .sorted(Comparator.comparing(CodigoDto::getCodigoId)) // Ordenar por codigoId
                    .collect(Collectors.toList());

            Map<Integer, TotalNovedadDto> totalesNovedad = totalNovedadList.stream()
                    .filter(Objects::nonNull)
                    .filter(total -> total.getCodigoId() != null)
                    .collect(Collectors.toMap(TotalNovedadDto::getCodigoId, total -> total));

            Map<Integer, TotalItemDto> totalesItem = totalItemList.stream()
                    .filter(Objects::nonNull)
                    .filter(total -> total.getCodigoId() != null)
                    .collect(Collectors.toMap(TotalItemDto::getCodigoId, total -> total));

            for (CodigoDto codigo : codigos) {
                logCodigo(codigo);
                var totalNovedad = BigDecimal.ZERO;
                var totalItem = BigDecimal.ZERO;
                if (totalesNovedad.containsKey(codigo.getCodigoId())) {
                    totalNovedad = totalesNovedad.get(codigo.getCodigoId()).getTotal();
                    }
                // Verificar si el totalNovedad es nulo o cero antes de procesarlo
                if (totalNovedad == null || totalNovedad.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }
                if (totalesItem.containsKey(codigo.getCodigoId())) {
                    totalItem = totalesItem.get(codigo.getCodigoId()).getTotal();
                }
                addCodigoDetails(document, codigo, detailTable, totalNovedad, totalItem);
            }

            document.add(detailTable);
            document.close();

        } catch (DocumentException | IOException e) {
            log.debug("Error generating report {}", e.getMessage());
        }
    }

    private void logCodigo(CodigoDto codigo) {
        try {
            log.debug("Codigo {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(codigo));
        } catch (JsonProcessingException e) {
            log.debug("Error serializando el codigo {}", e.getMessage());
        }
    }

    private void addTableHeader(PdfPTable table) {
        Font headerFont = new Font(Font.HELVETICA, 8, Font.BOLD);

        PdfPCell cell = new PdfPCell(new Phrase("", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Código", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Novedades", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Liquidado", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Diferencia", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addCodigoDetails(Document document, CodigoDto codigo, PdfPTable detailTable, BigDecimal totalNovedad, BigDecimal totalItem) throws DocumentException {

        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "ES"));

        // Formatear los totales
        String formattedTotalNovedad = numberFormat.format(totalNovedad);
        String formattedTotalItem = numberFormat.format(totalItem);
        String formattedDiferencia = numberFormat.format(totalItem.subtract(totalNovedad).abs()); // Tomar valor absoluto

        // Datos de la tabla
        var paragraph = new Paragraph();
        paragraph.add(new Phrase(Objects.requireNonNull(codigo.getCodigoId()).toString(), new Font(Font.HELVETICA, 8)));
        var cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(codigo.getNombre(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(formattedTotalNovedad.toString(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(formattedTotalItem.toString(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(formattedDiferencia.toString(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
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

            // Nueva fila para "Totales Generales"
            PdfPTable titleTable = new PdfPTable(1); // Solo una columna para centrar
            titleTable.setWidthPercentage(100);

            Paragraph titleParagraph = new Paragraph("Totales de Código", new Font(Font.HELVETICA, 13, Font.BOLD));
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
