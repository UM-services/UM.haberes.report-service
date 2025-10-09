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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistoricoAsignacionCargosService {
    private final Environment environment;
    private final CategoriaClient categoriaClient;
    private final CargoClient cargoClient;
    private final LiquidacionClient liquidacionClient;
    private final DependenciaClient dependenciaClient;
    private final GeograficaClient geograficaClient;
    private final PersonaClient personaClient;
    private final FacultadClient facultadClient;

    public String generate(Integer categoriaId) {
        log.debug("Starting categoriaId {}", categoriaId);
        String path = environment.getProperty("path.files");
        String filename = path + "historicoAsignacionDeCargos." + categoriaId + ".pdf";

        generateReport(filename, categoriaId);
        return filename;
    }

    private void generateReport(String filename, Integer categoriaId) {
        log.debug("Generating report for categoriaId {}", categoriaId);
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            HistoricoAsignacionCargosService.HeaderFooter event = new HistoricoAsignacionCargosService.HeaderFooter(categoriaId);
            writer.setPageEvent(event);
            document.open();

            // Encabezado del documento
            log.debug("Setting header");
            event.setHeader(document);

            // Obtener listas de datos
            List<GeograficaDto> geograficas = geograficaClient.findAll();
            if (geograficas == null) {
                log.error("No se pudieron recuperar las geografías.");
                return;
            }

            List<CategoriaDto> categorias = categoriaClient.findAll();
            if (categorias == null) {
                log.error("No se pudieron recuperar las categorias.");
                return;
            }

            List<DependenciaDto> dependencias = dependenciaClient.findAll();
            if (dependencias == null) {
                log.error("No se pudieron recuperar las dependencias.");
                return;
            }

            List<FacultadDto> facultades = facultadClient.findAll();
            if (facultades == null) {
                log.error("No se pudieron recuperar las facultades.");
                return;
            }

            Map<Integer, CategoriaDto> categoriaMap = categorias.stream()
                    .collect(Collectors.toMap(CategoriaDto::getCategoriaId, c -> c));

            Map<Integer, DependenciaDto> dependenciaMap = dependencias.stream()
                    .collect(Collectors.toMap(DependenciaDto::getDependenciaId, c -> c));

            Map<Integer, GeograficaDto> geograficaMap = geograficas.stream()
                    .collect(Collectors.toMap(GeograficaDto::getGeograficaId, g -> g));

            Map<Integer, FacultadDto> facultadMap = facultades.stream()
                    .collect(Collectors.toMap(FacultadDto::getFacultadId, f -> f));

            for (CategoriaDto categoria : categorias) {



            /*for (LiquidacionDto liquidacion : liquidaciones) {
                PersonaDto persona = personaClient.findByLegajoId(liquidacion.getLegajoId());
                GeograficaDto geografica = geograficaMap.get(liquidacion.getGeograficaId());
                DependenciaDto dependencia = dependenciaMap.get(liquidacion.getDependenciaId());
                FacultadDto facultad = facultadMap.get(liquidacion.getFacultadId());

                if (persona != null && geografica != null && dependencia != null && facultad != null) {
                    addHistoricoAsignacionCargosdetails(document, liquidacion, geografica, dependencia, persona, facultad);
                }
            }*/
        }


            document.close();
        } catch (DocumentException | IOException e) {
            log.debug("Error generating report {}", e.getMessage());
        }
    }

    private void addHistoricoAsignacionCargosdetails(Document document, LiquidacionDto liquidacion,  GeograficaDto geografica, DependenciaDto dependencia, PersonaDto persona, FacultadDto facultad) throws DocumentException {

        PdfPTable detailTable = new PdfPTable(1);
        detailTable.setWidthPercentage(100);

        // Periodo
        var paragraph = new Paragraph();
        paragraph.add(new Phrase("Periodo: ", new Font(Font.HELVETICA, 10)));
        paragraph.add(new Phrase(liquidacion.getAnho() + "/" + liquidacion.getMes(), new Font(Font.HELVETICA, 10, Font.BOLD)));
        var cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        detailTable.addCell(cell);
        document.add(detailTable);

        // Sede
        detailTable = new PdfPTable(1);
        detailTable.setWidthPercentage(85);
        paragraph = new Paragraph();
        paragraph.add(new Phrase("Sede: ", new Font(Font.HELVETICA, 10, Font.BOLD)));
        paragraph.add(new Phrase(geografica.getNombre(), new Font(Font.HELVETICA, 10)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        // Dependencia
        paragraph = new Paragraph();
        paragraph.add(new Phrase("Dependencia: ", new Font(Font.HELVETICA, 10, Font.BOLD)));
        paragraph.add(new Phrase(facultad.getNombre(), new Font(Font.HELVETICA, 10)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        document.add(detailTable);

        // Detalles del curso
        float[] columnHeader = {25, 80, 15};
        var legajoTable = new PdfPTable(columnHeader);
        legajoTable.setWidthPercentage(75);

        // Encabezado de la tabla
        paragraph = new Paragraph();
        paragraph.add(new Phrase("Legajo", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        legajoTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase("Apellido, Nombre", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        legajoTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase("categoria basico", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        legajoTable.addCell(cell);

        document.add(legajoTable);
    }

    static class HeaderFooter extends PdfPageEventHelper {
        private final Integer categoriaId;
        private PdfPTable headerTable;

        public HeaderFooter(Integer categoriaId) {
            this.categoriaId = categoriaId;
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
            paragraph.add(new Phrase("\nCategoria: ", new Font(Font.HELVETICA, 11)));
            paragraph.add(new Phrase(categoriaId.toString(), new Font(Font.HELVETICA, 11, Font.BOLD)));

            PdfPCell cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            headerTable.addCell(cell);

            // Nueva fila para titulo
            PdfPTable titleTable = new PdfPTable(1); // Solo una columna para centrar
            titleTable.setWidthPercentage(100);

            Paragraph titleParagraph = new Paragraph("Histórico de Asignaión de Cargos", new Font(Font.HELVETICA, 13, Font.BOLD));
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


