package um.haberes.report.service;

import lombok.RequiredArgsConstructor;
import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import um.haberes.report.client.haberes.core.CargoClaseClient;
import um.haberes.report.client.haberes.core.ClaseClient;
import um.haberes.report.kotlin.dto.haberes.core.*;


import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CargosClaseService {
    private final Environment environment;
    private final CargoClaseClient cargoClaseClient;
    private final ClaseClient claseClient;

    public String generate() {
        log.debug("Starting ");
        String path = environment.getProperty("path.files");
        String filename = path + "cargos-clase.pdf";

        generateReport(filename);
        return filename;
    }

    private void generateReport(String filename) {
        log.debug("Generating report");
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            CargosClaseService.HeaderFooter event = new CargosClaseService.HeaderFooter();
            writer.setPageEvent(event);
            document.open();

            // Encabezado del documento
            log.debug("Setting header");
            event.setHeader(document);

            // Obtener datos de clases y cargos
            List<ClaseDto> clases = claseClient.findAll();
            List<CargoClaseDto> cargos = cargoClaseClient.findAll();

            // Agrupar los cargos por clase y ordenarlos alfabéticamente
            Map<Integer, List<CargoClaseDto>> cargosPorClase = cargos.stream()
                    .collect(Collectors.groupingBy(CargoClaseDto::getClaseId, Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> list.stream().sorted(Comparator.comparing(CargoClaseDto::getNombre)).collect(Collectors.toList())
                    )));

            // Agregar clases y cargos
            for (ClaseDto clase : clases) {
                List<CargoClaseDto> cargosDeClase = cargosPorClase.get(clase.getClaseId());
                if (cargosDeClase != null && !cargosDeClase.isEmpty()) {
                    addCargosClaseDetails(document, clase, cargosDeClase);
                }
            }

            document.close();
        } catch (DocumentException | IOException e) {
            log.debug("Error generating report {}", e.getMessage());
        }
    }

    private void addCargosClaseDetails(Document document, ClaseDto clase, List<CargoClaseDto> cargos) throws DocumentException {

        document.add(new Paragraph("Clase " + clase.getClaseId() + " (" + clase.getValorHora() + ")", new Font(Font.HELVETICA, 12, Font.BOLD)));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);

        // Encabezado de la tabla
        Font headerFont = new Font(Font.HELVETICA, 8, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase("#", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Cargo", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        // Cargos de la clase
        for (CargoClaseDto cargo : cargos) {
            PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(cargo.getCargoClaseId()), new Font(Font.HELVETICA, 8)));
            idCell.setBorder(Rectangle.NO_BORDER);
            table.addCell(idCell);

            PdfPCell descCell = new PdfPCell(new Phrase(cargo.getNombre(), new Font(Font.HELVETICA, 8)));
            descCell.setBorder(Rectangle.NO_BORDER);
            table.addCell(descCell);
        }
        document.add(table);
        document.add(Chunk.NEWLINE);
    }

static class HeaderFooter extends PdfPageEventHelper {
        private PdfPTable headerTable;

        public HeaderFooter() {
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

            PdfPCell cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            headerTable.addCell(cell);

            // Nueva fila para titulo
            PdfPTable titleTable = new PdfPTable(1); // Solo una columna para centrar
            titleTable.setWidthPercentage(100);

            Paragraph titleParagraph = new Paragraph("Cargos con Clase", new Font(Font.HELVETICA, 13, Font.BOLD));
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

