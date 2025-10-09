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
import um.haberes.report.client.haberes.core.*;
import um.haberes.report.kotlin.dto.haberes.core.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CargosClasePeriodoService {

    private final Environment environment;
    private final CargoClaseDetalleClient cargoClaseDetalleClient;
    private final CargoClasePeriodoClient cargoClasePeriodoClient;
    private final PersonaClient personaClient;
    private final FacultadClient facultadClient;
    private final GeograficaClient geograficaClient;
    private final CargoClaseClient cargoClaseClient;
    private final ClaseClient claseClient;

    public String generate(Integer facultadId, Integer anho, Integer mes) {
        String path = environment.getProperty("path.files");
        String filename = path + "cargos-clase-periodo." + "." + facultadId + "." + anho  + mes + "." + ".pdf";

        generateReport(filename, facultadId, anho, mes);
        return filename;
    }

    private void generateReport(String filename, Integer facultadId, Integer anho, Integer mes) {
        log.debug("Generating report for year {}, month {}, facultad {}", anho, mes, facultadId);
        FacultadDto facultad = facultadClient.findByFacultadId(facultadId);
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            CargosClasePeriodoService.HeaderFooter event = new CargosClasePeriodoService.HeaderFooter(anho, mes, facultad);
            writer.setPageEvent(event);
            document.open();

            // Encabezado del documento
            log.debug("Setting header");
            event.setHeader(document);

            PdfPTable detailTable = new PdfPTable(8);
            float[] columnHeader = {10, 30, 35, 30, 10, 10, 10, 10};
            detailTable.setWidthPercentage(95);
            detailTable.setWidths(columnHeader);

            // Agregar encabezado de la tabla
            addTableHeader(detailTable);

            // Obtener listas de datos
            List<PersonaDto> personas = personaClient.findAllByFacultad(facultadId);
            if (personas == null || personas.isEmpty()) {
                log.error("No se encontraron personas para el reporte.");
                return;
            }

            List<GeograficaDto> geograficas = geograficaClient.findAll();
            if (geograficas == null) {
                log.error("No se pudieron recuperar las geografías.");
                return;
            }

            List<ClaseDto> clases = claseClient.findAll();
            if (clases == null) {
                log.error("No se pudieron recuperar las clases.");
                return;
            }

            Map<Integer, GeograficaDto> geograficaMap = geograficas.stream()
                    .collect(Collectors.toMap(GeograficaDto::getGeograficaId, g -> g));

            Map<Integer, ClaseDto> claseMap = clases.stream()
                    .collect(Collectors.toMap(ClaseDto::getClaseId, c -> c));

            Map<String, List<Object[]>> detallesPorSede = new HashMap<>();
            for (PersonaDto persona : personas) {
                List<CargoClaseDetalleDto> detalles = cargoClaseDetalleClient.findAllByLegajo(
                        persona.getLegajoId(), anho, mes);
                if (detalles == null || detalles.isEmpty()) {
                    log.warn("No se encontraron detalles de cargos para el legajo {} en el periodo {}-{}",
                            persona.getLegajoId(), anho, mes);
                    continue;
                }
                for (CargoClaseDetalleDto detalle : detalles) {
                    GeograficaDto sedeObj = geograficaMap.get(detalle.getGeograficaId());
                    String sedeNombre = (sedeObj != null) ? sedeObj.getNombre() : "Sin Sede";
                    detallesPorSede.computeIfAbsent(sedeNombre, k -> new ArrayList<>())
                            .add(new Object[]{persona, detalle});
                }
            }

            // Por cada sede, se crea una tabla con la misma estructura
            for (Map.Entry<String, List<Object[]>> entry : detallesPorSede.entrySet()) {
                String sedeNombre = entry.getKey();
                List<Object[]> registros = entry.getValue();

                // Agregar título de la sede
                Paragraph sedeTitle = new Paragraph("Sede: " + sedeNombre, new Font(Font.HELVETICA, 12, Font.BOLD));
                sedeTitle.setAlignment(Element.ALIGN_LEFT);
                document.add(sedeTitle);
                document.add(Chunk.NEWLINE);

                // Crear la tabla con 8 columnas
                PdfPTable sedeTable = new PdfPTable(8);
                float[] tableHeader = {10, 30, 35, 30, 10, 10, 10, 10};
                sedeTable.setWidthPercentage(95);
                sedeTable.setWidths(tableHeader);

                // Agregar encabezado a la tabla
                addTableHeader(sedeTable);

                // Iterar sobre cada registro (persona y detalle) para agregar las filas a la tabla
                for (Object[] registro : registros) {
                    PersonaDto persona = (PersonaDto) registro[0];
                    CargoClaseDetalleDto detalle = (CargoClaseDetalleDto) registro[1];

                    // Buscar el período correspondiente
                    List<CargoClasePeriodoDto> periodos = cargoClasePeriodoClient.findAllByLegajo(persona.getLegajoId());
                    Map<Long, CargoClasePeriodoDto> periodoMap = periodos.stream()
                            .collect(Collectors.toMap(
                                    p -> p.getCargoClaseId(),
                                    Function.identity(),
                                    (p1, p2) -> p1));
                    CargoClasePeriodoDto cargoClasePeriodo = periodoMap.get(detalle.getCargoClaseId());
                    if (cargoClasePeriodo == null) {
                        log.warn("No se encontró período para cargoClaseId {}", detalle.getCargoClaseId());
                        continue;
                    }
                    CargoClaseDto cargoClase = cargoClaseClient.findByCargoClaseId(detalle.getCargoClaseId());
                    if (cargoClase == null) {
                        log.warn("No se encontró cargo clase para cargoClaseId {}", detalle.getCargoClaseId());
                        continue;
                    }
                    ClaseDto clase = claseMap.get(cargoClase.getClaseId());
                    GeograficaDto sede = geograficaMap.get(detalle.getGeograficaId());
                    if (clase == null || sede == null) {
                        log.warn("Datos incompletos para cargoClaseId {}", detalle.getCargoClaseId());
                        continue;
                    }
                    // Agregar la fila de detalles a la tabla
                    addCargosClasePeriodoDetails(document, sedeTable, cargoClasePeriodo, persona, facultad, sede, cargoClase, clase);
                }
                // Agregar la tabla de la sede al documento
                document.add(sedeTable);
                document.add(Chunk.NEWLINE);
            }
            document.close();
        } catch (DocumentException | IOException e) {
            log.debug("Error generating report {}", e.getMessage());
        }
    }

    private void addTableHeader(PdfPTable table) {
        Font headerFont = new Font(Font.HELVETICA, 8, Font.BOLD);

        PdfPCell cell = new PdfPCell(new Phrase("Legajo", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Apellido, Nombre", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Cargo", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Descripción", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Desde", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Hasta", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Horas", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Clase", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }

    private void addCargosClasePeriodoDetails(Document document, PdfPTable detailTable, CargoClasePeriodoDto cargoClasePeriodo, PersonaDto persona, FacultadDto facultad, GeograficaDto geografica, CargoClaseDto cargoClase, ClaseDto clase) throws DocumentException {

        // Datos de la tabla
        var paragraph = new Paragraph();
        paragraph.add(new Phrase(Objects.requireNonNull(persona.getLegajoId()).toString(), new Font(Font.HELVETICA, 8)));
        var cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(persona.getApellido() + ", " + persona.getNombre(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(cargoClase.getNombre(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(cargoClasePeriodo.getDescripcion(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(cargoClasePeriodo.getPeriodoDesde().toString(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(cargoClasePeriodo.getPeriodoHasta().toString(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(cargoClasePeriodo.getHoras() + " ", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(clase.getNombre(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);
    }

    static class HeaderFooter extends PdfPageEventHelper {
        private final Integer anho;
        private final Integer mes;
        private final FacultadDto facultad;
        private PdfPTable headerTable;

        public HeaderFooter(Integer anho, Integer mes, FacultadDto facultad) {
            this.anho = anho;
            this.mes = mes;
            this.facultad = facultad;
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

            // Nueva fila para facultad
            PdfPTable facultadTable = new PdfPTable(1); // Solo una columna para centrar
            facultadTable.setWidthPercentage(100);

            Paragraph facultadParagraph = new Paragraph(facultad.getNombre(), new Font(Font.HELVETICA, 11, Font.BOLD));
            facultadParagraph.setAlignment(Element.ALIGN_RIGHT);

            // Nueva fila para titulo
            PdfPTable titleTable = new PdfPTable(1); // Solo una columna para centrar
            titleTable.setWidthPercentage(100);

            Paragraph titleParagraph = new Paragraph("Cargos con Clase por Periodo", new Font(Font.HELVETICA, 13, Font.BOLD));
            titleParagraph.setAlignment(Element.ALIGN_CENTER);

            PdfPCell titleCell = new PdfPCell(titleParagraph);
            PdfPCell facultadCell = new PdfPCell(facultadParagraph);
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            facultadCell.setBorder(Rectangle.NO_BORDER);
            facultadCell.setHorizontalAlignment(Element.ALIGN_TOP);
            facultadCell.setVerticalAlignment(Element.ALIGN_RIGHT);
            titleTable.addCell(titleCell);
            facultadTable.addCell(facultadCell);

            document.add(headerTable);
            document.add(Chunk.NEWLINE);
            document.add(facultadTable); // Agregar facultad
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