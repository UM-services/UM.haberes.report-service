package um.haberes.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import um.haberes.report.client.haberes.core.*;
import um.haberes.report.client.tesoreria.core.CursoCargoContratadoClient;
import um.haberes.report.client.tesoreria.core.PersonaClient;
import um.haberes.report.kotlin.dto.haberes.core.CursoCargoDto;
import um.haberes.report.kotlin.dto.haberes.core.CursoDto;
import um.haberes.report.kotlin.dto.haberes.core.FacultadDto;
import um.haberes.report.kotlin.dto.haberes.core.GeograficaDto;
import um.haberes.report.kotlin.dto.tesoreria.core.CursoCargoContratadoDto;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocentesSedeService {

    private final Environment environment;
    private final FacultadClient facultadClient;
    private final GeograficaClient geograficaClient;
    private final CursoClient cursoClient;
    private final CursoCargoClient cursoCargoClient;
    private final CursoCargoContratadoClient cursoCargoContratadoClient;
    private final CargoTipoClient cargoTipoClient;
    private final DesignacionTipoClient designacionTipoClient;

    // tesoreria-core-service/persona
    private final PersonaClient personaClient;

    private FacultadDto facultad;
    private GeograficaDto geografica;

    public String generate(Integer facultadId, Integer geograficaId, Integer anho, Integer mes) {
        String path = environment.getProperty("path.files");
        String filename = path + "docentes." + facultadId + "." + geograficaId + "." + anho + "." + mes + "." + facultadId + ".pdf";
        facultad = facultadClient.findByFacultadId(facultadId);
        try {
            log.debug("Facultad -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(facultad));
        } catch (JsonProcessingException e) {
            log.debug("facultad -> null");
        }
        geografica = geograficaClient.findByGeograficaId(geograficaId);
        try {
            log.debug("Geografica -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(geografica));
        } catch (JsonProcessingException e) {
            log.debug("geografica -> null");
        }

        generateReport(filename, facultadId, geograficaId, anho, mes);
        return filename;
    }

    private void generateReport(String filename, Integer facultadId, Integer geograficaId, Integer anho, Integer mes) {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            HeaderFooter event = new HeaderFooter(facultad, geografica, anho, mes);
            writer.setPageEvent(event);
            document.open();

            // Encabezado del documento
            event.setHeader(document);

            // Agregar contenido del reporte
            for (CursoDto curso : cursoClient.findAllByFacultadIdAndGeograficaIdAndAnhoAndMes(facultadId, geograficaId, anho, mes)) {
                // Encabezado del curso
                event.setCursoHeader(document, curso);

                // Detalles del curso
                addCursoDetails(document, curso, anho, mes);
            }

            document.close();
        } catch (DocumentException | IOException e) {
            log.debug("Error generating report {}", e.getMessage());
        }
    }

    private void addCursoDetails(Document document, CursoDto curso, Integer anho, Integer mes) throws DocumentException {
        PdfPTable detailTable = new PdfPTable(5);
        detailTable.setWidthPercentage(95);
        detailTable.setWidths(new int[]{25, 110, 35, 20, 10});

        // Cabeceras de columnas
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Phrase("Cargo", new Font(Font.HELVETICA, 8, Font.BOLD)));
        PdfPCell cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase("Docente", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase("Designación", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase("Horas", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase("Des", new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        detailTable.addCell(cell);

        // Datos de los detalles
        for (CursoCargoDto cursoCargo : cursoCargoClient.findAllByCurso(curso.getCursoId(), anho, mes)) {
            paragraph = new Paragraph();
            paragraph.add(new Phrase(cursoCargo.getCargoTipo().getNombre(), new Font(Font.HELVETICA, 8)));
            cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            detailTable.addCell(cell);

            paragraph = new Paragraph();
            paragraph.add(new Phrase(cursoCargo.getPersona().getApellidoNombre(), new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            detailTable.addCell(cell);

            paragraph = new Paragraph();
            paragraph.add(new Phrase(cursoCargo.getDesignacionTipo().getNombre(), new Font(Font.HELVETICA, 8)));
            cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            detailTable.addCell(cell);

            paragraph = new Paragraph();
            paragraph.add(new Phrase(cursoCargo.getHorasSemanales().toString(), new Font(Font.HELVETICA, 8)));
            cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            detailTable.addCell(cell);

            String desarraigo = "";
            if (cursoCargo.getDesarraigo() == 1) {
                desarraigo = "*";
            }

            paragraph = new Paragraph();
            paragraph.add(new Phrase(desarraigo, new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            detailTable.addCell(cell);
        }

        // Datos de los contratados
        for (CursoCargoContratadoDto cursoCargoContratado : cursoCargoContratadoClient.findAllByCurso(curso.getCursoId(), anho, mes)) {
            try {
                log.debug("CursoCargoContratado -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(cursoCargoContratado));
            } catch (JsonProcessingException e) {
                log.debug("cursoCargoContratado -> null");
            }
            var cargoTipo = cargoTipoClient.findByCargoTipoId(cursoCargoContratado.getCargoTipoId());
            paragraph = new Paragraph();
            paragraph.add(new Phrase(cargoTipo.getNombre(), new Font(Font.HELVETICA, 8)));
            cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            detailTable.addCell(cell);

            var persona = personaClient.findByUnique(cursoCargoContratado.getContratadoPersona().getPersonaId(), cursoCargoContratado.getContratadoPersona().getDocumentoId());
            paragraph = new Paragraph();
            paragraph.add(new Phrase(persona.getApellidoNombre(), new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            detailTable.addCell(cell);

            paragraph = new Paragraph();
            paragraph.add(new Phrase("Contratado", new Font(Font.HELVETICA, 8)));
            cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            detailTable.addCell(cell);

            paragraph = new Paragraph();
            paragraph.add(new Phrase(cursoCargoContratado.getHorasSemanales().toString(), new Font(Font.HELVETICA, 8)));
            cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            detailTable.addCell(cell);

            // Desarraigo
            paragraph = new Paragraph();
            paragraph.add(new Phrase("", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            detailTable.addCell(cell);
        }

        // Agrego una línea en blanco para separar los cursos
        paragraph = new Paragraph();
        paragraph.add(new Phrase("", new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase("", new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase("", new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase("", new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase("", new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        detailTable.addCell(cell);

        document.add(detailTable);
        document.add(Chunk.NEWLINE);
    }

    @Slf4j
    static class HeaderFooter extends PdfPageEventHelper {
        private final FacultadDto facultad;
        private final GeograficaDto geografica;
        private final Integer anho;
        private final Integer mes;
        private PdfPTable headerTable;

        public HeaderFooter(FacultadDto facultad, GeograficaDto geografica, Integer anho, Integer mes) {
            this.facultad = facultad;
            this.geografica = geografica;
            this.anho = anho;
            this.mes = mes;
        }

        public void setHeader(Document document) throws DocumentException, IOException {
            float[] columnHeader = {2, 8};
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
            paragraph.add(new Phrase("Universidad de Mendoza", new Font(Font.HELVETICA, 15, Font.BOLD)));
            paragraph.add(new Phrase("\n" + facultad.getNombre(), new Font(Font.HELVETICA, 13, Font.BOLD)));
            paragraph.add(new Phrase("\n\nDocentes por Sede", new Font(Font.HELVETICA, 14, Font.BOLD)));
            paragraph.add(new Phrase("\n\nPeriodo: ", new Font(Font.HELVETICA, 11)));
            paragraph.add(new Phrase(mes + "/" + anho, new Font(Font.HELVETICA, 11, Font.BOLD)));
            paragraph.add(new Phrase("\nSede: ", new Font(Font.HELVETICA, 11)));
            paragraph.add(new Phrase(geografica.getNombre(), new Font(Font.HELVETICA, 11, Font.BOLD)));

            PdfPCell cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            headerTable.addCell(cell);
            log.debug("document -> {}", document);
            document.add(headerTable);
            document.add(Chunk.NEWLINE);
        }

        public void setCursoHeader(Document document, CursoDto curso) throws DocumentException {
            float[] columnHeader = {8.5f, 1.5f};
            PdfPTable cursoTable = new PdfPTable(columnHeader);
            cursoTable.setWidthPercentage(100);

            Paragraph paragraph = new Paragraph();
            paragraph.add(new Phrase("Curso: ", new Font(Font.HELVETICA, 9)));
            paragraph.add(new Phrase(curso.getNombre(), new Font(Font.HELVETICA, 9, Font.BOLD)));
            PdfPCell cursoHeaderCell = new PdfPCell(paragraph);
            cursoHeaderCell.setBorder(Rectangle.NO_BORDER);
            cursoTable.addCell(cursoHeaderCell);

            String periodo = "";
            if (curso.getAnual() == 1) {
                periodo = "Anual";
            }
            if (curso.getSemestre1() == 1) {
                periodo = "1er Semestre";
            }
            if (curso.getSemestre2() == 1) {
                periodo = "2do Semestre";
            }
            paragraph = new Paragraph();
            paragraph.add(new Phrase(periodo, new Font(Font.HELVETICA, 9)));
            cursoHeaderCell = new PdfPCell(paragraph);
            cursoHeaderCell.setBorder(Rectangle.NO_BORDER);
            cursoHeaderCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cursoTable.addCell(cursoHeaderCell);

            document.add(cursoTable);
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
