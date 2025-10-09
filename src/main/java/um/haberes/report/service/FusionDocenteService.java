package um.haberes.report.service;

import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import um.haberes.report.client.haberes.core.*;
import um.haberes.report.kotlin.dto.haberes.core.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FusionDocenteService {

    private final Environment environment;
    private final CursoFusionClient cursoFusionClient;
    private final FacultadClient facultadClient;
    private final GeograficaClient geograficaClient;
    private final CategoriaClient categoriaClient;
    private final PersonaClient personaClient;

    public String generate(Integer anho, Integer mes) {
        String path = environment.getProperty("path.files");
        String filename = path + "fusion.docente." + "." + anho + "." + mes + ".pdf";

        generateReport(filename, anho, mes);
        return filename;
    }

    private void generateReport(String filename, Integer anho, Integer mes) {
        log.debug("Generating report for anho {}, mes {}", anho, mes);
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            HeaderFooter event = new HeaderFooter(anho, mes);
            writer.setPageEvent(event);
            document.open();

            // Encabezado del documento
            log.debug("Setting header");
            event.setHeader(document);

            PdfPTable detailTable = new PdfPTable(6);
            float[] columnHeader = {10, 30, 45, 15, 15, 15};
            detailTable.setWidthPercentage(95);
            detailTable.setWidths(columnHeader);

            // Agregar encabezado de la tabla
            addTableHeader(detailTable);

            List<CategoriaDto> categorias = categoriaClient.findAll();
            if (categorias == null || categorias.isEmpty()) {
                log.warn("No se encontraron categorias para el periodo {}-{}", anho, mes);
                return;
            }

            List<PersonaDto> personas = personaClient.findAll();
            if (personas == null) {
                log.error("No se pudieron recuperar las personas.");
                return;
            }

            List<FacultadDto> facultades = facultadClient.findAll();
            if (facultades == null) {
                log.error("No se pudieron recuperar las facultades.");
                return;
            }

            List<GeograficaDto> geografias = geograficaClient.findAll();
            if (geografias == null) {
                log.error("No se pudieron recuperar las geografías.");
                return;
            }

            // Obtener listas completas y mapearlas en memoria
            Map<Long, PersonaDto> personaMap = personaClient.findAll().stream()
                    .collect(Collectors.toMap(PersonaDto::getLegajoId, persona -> persona));

            Map<Integer, FacultadDto> facultadMap = facultadClient.findAll().stream()
                    .collect(Collectors.toMap(FacultadDto::getFacultadId, facultad -> facultad));

            Map<Integer, GeograficaDto> geograficaMap = geograficaClient.findAll().stream()
                    .collect(Collectors.toMap(GeograficaDto::getGeograficaId, geografica -> geografica));

            Map<Integer, CategoriaDto> categoriaMap = categoriaClient.findAll().stream()
                    .collect(Collectors.toMap(CategoriaDto::getCategoriaId, categoria -> categoria));

            for (PersonaDto persona : personas) {
                List<CursoFusionDto> cursoFusions = cursoFusionClient.findAllByLegajoId(persona.getLegajoId(), anho, mes);
                if (cursoFusions == null || cursoFusions.isEmpty()) {
                    log.warn("No se encontraron cursos fusionados para el legajo {} en el periodo {}-{}", persona.getLegajoId(), anho, mes);
                    continue;
                }
                cursoFusions = cursoFusions.stream()
                        .filter(Objects::nonNull) // Asegurarse de no tener valores nulos
                        .sorted(Comparator.comparing(CursoFusionDto::getLegajoId)) // Ordenar por LegajoId
                        .collect(Collectors.toList()); // Correcta ubicación del paréntesis

                for (CursoFusionDto cursoFusion : cursoFusions) {
                    FacultadDto facultad = facultadMap.get(cursoFusion.getFacultadId());
                    GeograficaDto geografica = geograficaMap.get(cursoFusion.getGeograficaId());
                    CategoriaDto categoria = categoriaMap.get(cursoFusion.getCategoriaId());
                    if (facultad != null && geografica != null && categoria != null && categoria.getBasico() != null) {
                        addFusionDetails(document, detailTable, cursoFusion, persona, facultad, geografica, categoria);
                    }
                }
            }
            document.add(detailTable);
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

        cell = new PdfPCell(new Phrase("Facultad", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Sede", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Cargo", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Básico", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }

    private void addFusionDetails(Document document, PdfPTable detailTable, CursoFusionDto cursoFusion, PersonaDto persona, FacultadDto facultad, GeograficaDto geografica, CategoriaDto categoria) throws DocumentException {

        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "ES"));

        // Formatear los totales
        String formattedBasico = numberFormat.format(categoria.getBasico());

        // Datos de la tabla
        var paragraph = new Paragraph();
        paragraph.add(new Phrase(Objects.requireNonNull(cursoFusion.getLegajoId()).toString(), new Font(Font.HELVETICA, 8)));
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
        paragraph.add(new Phrase(facultad.getNombre(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(geografica.getNombre(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(categoria.getNombre(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(formattedBasico.toString(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        detailTable.addCell(cell);
    }

    @Slf4j
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

            Paragraph titleParagraph = new Paragraph("Fusión por Docente", new Font(Font.HELVETICA, 13, Font.BOLD));
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
