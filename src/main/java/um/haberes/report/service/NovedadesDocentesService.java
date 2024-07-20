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
import um.haberes.report.client.CursoCargoNovedadClient;
import um.haberes.report.client.FacultadClient;
import um.haberes.report.client.GeograficaClient;
import um.haberes.report.kotlin.dto.CursoCargoNovedadDto;
import um.haberes.report.kotlin.dto.FacultadDto;
import um.haberes.report.kotlin.dto.GeograficaDto;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class NovedadesDocentesService {

    private final Environment environment;
    private final CursoCargoNovedadClient cursoCargoNovedadClient;
    private final FacultadClient facultadClient;
    private final GeograficaClient geograficaClient;

    public NovedadesDocentesService(Environment environment, CursoCargoNovedadClient cursoCargoNovedadClient, FacultadClient facultadClient, GeograficaClient geograficaClient) {
        this.environment = environment;
        this.cursoCargoNovedadClient = cursoCargoNovedadClient;
        this.facultadClient = facultadClient;
        this.geograficaClient = geograficaClient;
    }

    public String generate(Integer facultadId, Integer anho, Integer mes) {
        log.debug("Starting facultadId {}, anho {}, mes {}", facultadId, anho, mes);
        String path = environment.getProperty("path.files");
        String filename = path + "novedades." + facultadId + "." + anho + "." + mes + ".pdf";

        generateReport(filename, facultadId, anho, mes);
        return filename;
    }

    private void generateReport(String filename, Integer facultadId, Integer anho, Integer mes) {
        log.debug("Generating report for facultadId {}, anho {}, mes {}", facultadId, anho, mes);
        FacultadDto facultad = facultadClient.findByFacultadId(facultadId);
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            HeaderFooter event = new HeaderFooter(anho, mes, facultad);
            writer.setPageEvent(event);
            document.open();

            // Encabezado del documento
            log.debug("Setting header");
            event.setHeader(document);

            // Busca las sedes con novedades
            log.debug("Finding sedes with novedades");
            List<Integer> geograficaIds = cursoCargoNovedadClient.findAllByFacultad(facultadId, anho, mes).stream().map(cursoCargoNovedad -> cursoCargoNovedad.getCurso().getGeograficaId()).toList();

            for (GeograficaDto geografica : geograficaClient.findAllByGeograficaIdIn(geograficaIds)) {
                var detailTable = new PdfPTable(1);
                detailTable.setWidthPercentage(100);
                var paragraph = new Paragraph();
                paragraph.add(new Phrase("\nSede: ", new Font(Font.HELVETICA, 11)));
                paragraph.add(new Phrase(geografica.getNombre(), new Font(Font.HELVETICA, 11, Font.BOLD)));
                var cell = new PdfPCell(paragraph);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                detailTable.addCell(cell);
                document.add(detailTable);

                // Agregar contenido de altas del reporte
                log.debug("Finding altas");
                var primera = true;
                for (CursoCargoNovedadDto novedad : cursoCargoNovedadClient.findAllByFacultadAndGeograficaAndAlta(facultadId, geografica.getGeograficaId(), anho, mes)) {
                    if (primera) {
                        primera = false;
                        detailTable = new PdfPTable(1);
                        detailTable.setWidthPercentage(70);
                        paragraph = new Paragraph();
                        paragraph.add(new Phrase("ALTA", new Font(Font.HELVETICA, 11, Font.BOLD)));
                        cell = new PdfPCell(paragraph);
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        detailTable.addCell(cell);
                        document.add(detailTable);
                    }
                    addNovedadDetails(document, novedad);
                }

                // Agregar contenido de cambios del reporte
                log.debug("Finding cambios");
                primera = true;
                for (CursoCargoNovedadDto novedad : cursoCargoNovedadClient.findAllByFacultadAndGeograficaAndCambio(facultadId, geografica.getGeograficaId(), anho, mes)) {
                    if (primera) {
                        primera = false;
                        detailTable = new PdfPTable(1);
                        detailTable.setWidthPercentage(70);
                        paragraph = new Paragraph();
                        paragraph.add(new Phrase("CAMBIO", new Font(Font.HELVETICA, 11, Font.BOLD)));
                        cell = new PdfPCell(paragraph);
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        detailTable.addCell(cell);
                        document.add(detailTable);
                    }
                    addNovedadDetails(document, novedad);
                }

                // Agregar contenido de bajas del reporte
                log.debug("Finding bajas");
                primera = true;
                for (CursoCargoNovedadDto novedad : cursoCargoNovedadClient.findAllByFacultadAndGeograficaAndBaja(facultadId, geografica.getGeograficaId(), anho, mes)) {
                    if (primera) {
                        primera = false;
                        detailTable = new PdfPTable(1);
                        detailTable.setWidthPercentage(70);
                        paragraph = new Paragraph();
                        paragraph.add(new Phrase("BAJA", new Font(Font.HELVETICA, 11, Font.BOLD)));
                        cell = new PdfPCell(paragraph);
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        detailTable.addCell(cell);
                        document.add(detailTable);
                    }
                    addNovedadDetails(document, novedad);
                }

            }

            document.close();
        } catch (DocumentException | IOException e) {
            log.debug("Error generating report {}", e.getMessage());
        }
    }

    private void addNovedadDetails(Document document, CursoCargoNovedadDto novedad) throws DocumentException {
        try {
            log.debug("Adding novedad {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(novedad));
        } catch (JsonProcessingException e) {
            log.debug("Error serializing novedad {}", e.getMessage());
        }
        PdfPTable detailTable = new PdfPTable(1);
        detailTable.setWidthPercentage(100);

        // Encabezado del curso
        var paragraph = new Paragraph();
        paragraph.add(new Phrase("Curso: ", new Font(Font.HELVETICA, 10)));
        paragraph.add(new Phrase(novedad.getCurso().getNombre(), new Font(Font.HELVETICA, 10, Font.BOLD)));
        var cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        detailTable.addCell(cell);
        document.add(detailTable);

        // Detalles del curso
        float[] columnHeader = {25, 100, 15, 20, 20, 20};
        var cargoTable = new PdfPTable(columnHeader);
        cargoTable.setWidthPercentage(95);

        // Encabezado de la tabla
        paragraph = new Paragraph();
        paragraph.add(new Phrase("Cargo", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cargoTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase("Docente", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cargoTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase("Horas", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cargoTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase("Desarraigo", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cargoTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase("Aut.", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cargoTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase("Rech.", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cargoTable.addCell(cell);

        // Datos de la tabla
        paragraph = new Paragraph();
        paragraph.add(new Phrase(novedad.getCargoTipo().getNombre(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cargoTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(novedad.getPersona().getApellidoNombre(), new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cargoTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(novedad.getHorasSemanales().toString(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cargoTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(novedad.getDesarraigo() == 1 ? "*" : "", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cargoTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(novedad.getAutorizado() == 1 ? "*" : "", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cargoTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(novedad.getRechazado() == 1 ? "*" : "", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cargoTable.addCell(cell);

        document.add(cargoTable);

        // Observaciones
        detailTable = new PdfPTable(1);
        detailTable.setWidthPercentage(85);
        paragraph = new Paragraph();
        paragraph.add(new Phrase("Observaciones: ", new Font(Font.HELVETICA, 10, Font.BOLD)));
        paragraph.add(new Phrase(novedad.getSolicitud(), new Font(Font.HELVETICA, 10)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        // Respuesta
        paragraph = new Paragraph();
        paragraph.add(new Phrase("Respuesta: ", new Font(Font.HELVETICA, 10, Font.BOLD)));
        paragraph.add(new Phrase(novedad.getRespuesta(), new Font(Font.HELVETICA, 10)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        document.add(detailTable);
//        document.add(Chunk.NEWLINE);
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
            float[] columnHeader = {1, 4};
            headerTable = new PdfPTable(columnHeader);
            headerTable.setWidthPercentage(100);

            // Imagen de la Universidad
            log.debug("Imagen de la Universidad");
            Image image = Image.getInstance("marca_um.png");
            PdfPCell imageCell = new PdfPCell(image);
            imageCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(imageCell);

            // Texto del encabezado
            log.debug("Texto del encabezado");
            Paragraph paragraph = new Paragraph();
            paragraph.add(new Phrase("UNIVERSIDAD DE MENDOZA", new Font(Font.HELVETICA, 15, Font.BOLD)));
            paragraph.add(new Phrase("\n" + facultad.getNombre(), new Font(Font.HELVETICA, 12, Font.BOLD)));
            paragraph.add(new Phrase("\n\nNovedades Docentes", new Font(Font.HELVETICA, 13, Font.BOLD)));
            paragraph.add(new Phrase("\n\nPeriodo: ", new Font(Font.HELVETICA, 11)));
            paragraph.add(new Phrase(mes + "/" + anho, new Font(Font.HELVETICA, 11, Font.BOLD)));
            PdfPCell cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            headerTable.addCell(cell);

            document.add(headerTable);
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
