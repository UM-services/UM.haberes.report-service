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
import um.haberes.report.client.haberes.core.DocenteDesignacionClient;
import um.haberes.report.client.haberes.core.FacultadClient;
import um.haberes.report.client.haberes.core.GeograficaClient;
import um.haberes.report.client.haberes.core.PersonaClient;
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
public class DesignacionesDocenteService {
    private final Environment environment;
    private final DocenteDesignacionClient docenteDesignacionClient;
    private final GeograficaClient geograficaClient;
    private final PersonaClient personaClient;
    private final FacultadClient facultadClient;

    public String generate(Integer anho, Integer mes) {
        String path = environment.getProperty("path.files");
        String filename = path + "designaciones-docente." + anho + "." + mes + ".pdf";

        generateReport(filename, anho, mes);
        return filename;
    }

    private void generateReport(String filename, Integer anho, Integer mes) {
        log.debug("Generating report for anho {}, mes {}", anho, mes);
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            log.debug("writer created");
            DesignacionesDocenteService.HeaderFooter event = new DesignacionesDocenteService.HeaderFooter(anho, mes);
            log.debug("event created");
            writer.setPageEvent(event);
            log.debug("page event set");
            document.open();
            log.debug("document opened");

            // Encabezado del documento
            event.setHeader(document);
            log.debug("header set");

            List<PersonaDto> personas = personaClient.findAllDocente(anho, mes);
            if (personas == null || personas.isEmpty()) {
                log.warn("No se encontraron docentes");
                return;
            }

            // Obtener todas las designaciones del periodo
            List<DocenteDesignacionDto> designaciones = docenteDesignacionClient.findAllByPeriodo(anho, mes);
            if (designaciones == null || designaciones.isEmpty()) {
                log.warn("No se encontraron designaciones");
                return;
            }

            Set<String> designacionesUnicas = designaciones.stream()
                    .map(d -> d.getLegajoId() + "-" + d.getFacultadId() + "-" + d.getGeograficaId() + "-" + d.getEspacio())
                    .collect(Collectors.toSet());

            log.info("Total designaciones: {}, únicas (por legajo+facultad+sede+espacio): {}", designaciones.size(), designacionesUnicas.size());

            // Agrupar designaciones por legajo
            Map<Long, List<DocenteDesignacionDto>> designacionesPorLegajo = designaciones.stream()
                    .filter(d -> d.getLegajoId() != null)
                    .collect(Collectors.groupingBy(DocenteDesignacionDto::getLegajoId));

            // Filtrar docentes que tienen designaciones
            List<PersonaDto> docentesConDesignaciones = personas.stream()
                    .filter(p -> p.getLegajoId() != null)
                    .filter(p -> designacionesPorLegajo.containsKey(p.getLegajoId()))
                    .sorted(Comparator.comparing(PersonaDto::getApellido))
                    .collect(Collectors.toList());

            if (docentesConDesignaciones.isEmpty()) {
                log.warn("No se encontraron docentes con designaciones");
                return;
            }

            // Obtener IDs únicos
            Set<Integer> facultadIds = designaciones.stream()
                    .map(DocenteDesignacionDto::getFacultadId)
                    .collect(Collectors.toSet());

            Set<Integer> geograficaIds = designaciones.stream()
                    .map(DocenteDesignacionDto::getGeograficaId)
                    .collect(Collectors.toSet());

            Map<Integer, FacultadDto> facultadesMap = facultadClient.findAllFacultades().stream()
                    .filter(f -> facultadIds.contains(f.getFacultadId()))
                    .collect(Collectors.toMap(FacultadDto::getFacultadId, f -> f));

            Map<Integer, GeograficaDto> geograficaMap = geograficaClient.findAllByGeograficaIdIn(new ArrayList<>(geograficaIds)).stream()
                    .collect(Collectors.toMap(GeograficaDto::getGeograficaId, g -> g));

            // Ordenar docentes
            for (PersonaDto docente : docentesConDesignaciones) {
                Long legajoId = docente.getLegajoId();
                List<DocenteDesignacionDto> listaDesignaciones = designacionesPorLegajo.get(legajoId).stream()
                        .sorted(Comparator.comparing(DocenteDesignacionDto::getEspacio, Comparator.nullsLast(String::compareTo)))
                        .collect(Collectors.toList());


                // Agregar encabezado del docente
                Paragraph docenteTitle = new Paragraph();
                docenteTitle.add(new Phrase(docente.getLegajoId() + ". ", new Font(Font.HELVETICA, 9, Font.BOLD)));
                docenteTitle.add(new Phrase(docente.getApellido() + ", " + docente.getNombre(), new Font(Font.HELVETICA, 9, Font.BOLD)));
                docenteTitle.setSpacingBefore(10);
                docenteTitle.setSpacingAfter(5);
                document.add(docenteTitle);

                // Crear tabla
                PdfPTable detailTable = new PdfPTable(10);
                float[] columnWidths = {30, 17, 43, 10, 15, 15, 10, 12, 10, 10};
                detailTable.setWidthPercentage(95);
                detailTable.setWidths(columnWidths);
                addTableHeader(detailTable);

                for (DocenteDesignacionDto d : listaDesignaciones) {
                    FacultadDto facultad = facultadesMap.get(d.getFacultadId());
                    GeograficaDto geografica = geograficaMap.get(d.getGeograficaId());

                    log.debug("FACULTAD: {}, SEDE: {}, ESPACIO: {}, HORAS: {}, CARGO: {}",
                            facultad != null ? facultad.getNombre() : "N/A",
                            geografica != null ? geografica.getNombre() : "N/A",
                            d.getEspacio(),
                            d.getHorasSemanales(),
                            d.getCargo()
                    );

                    addDesignacionesDocenteDetails(document, detailTable, d, geografica, facultad);
                }

                document.add(detailTable);
                document.add(Chunk.NEWLINE);
            }

            document.close();
        } catch (Exception e) {
            log.error("Error generating report {}", e.getMessage(), e);
        }
    }

    private String getDictado(DocenteDesignacionDto docenteDesignacion) {
        boolean esAnual = docenteDesignacion.getAnual() != null && docenteDesignacion.getAnual() != 0;
        boolean esSem1 = docenteDesignacion.getSemestre1() != null && docenteDesignacion.getSemestre1() != 0;
        boolean esSem2 = docenteDesignacion.getSemestre2() != null && docenteDesignacion.getSemestre2() != 0;

        if (esAnual) {
            return "Anual";
        }
        if (esSem1 && esSem2) {
            return "Semestral";
        }
        if (esSem1) {
            return "Semestre 1";
        }
        if (esSem2) {
            return "Semestre 2";
        }
        return "No especificado";
    }

    private void addTableHeader(PdfPTable table) {
        Font headerFont = new Font(Font.HELVETICA, 8, Font.BOLD);

        PdfPCell cell = new PdfPCell(new Phrase("Facultad", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Sede", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Espacio", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("H/Sem", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Cargo", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Designac.", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("H/Des", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Dictado", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("H/Eqv", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }

    private void addDesignacionesDocenteDetails(Document document, PdfPTable detailTable, DocenteDesignacionDto docenteDesignacion, GeograficaDto geografica, FacultadDto facultad) throws DocumentException {
        String dictado = getDictado(docenteDesignacion);
        // Datos de la tabla
        var paragraph = new Paragraph();
        paragraph.add(new Phrase(facultad.getNombre(), new Font(Font.HELVETICA, 8)));
        var cell = new PdfPCell(paragraph);
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
        paragraph.add(new Phrase(docenteDesignacion.getEspacio(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(String.valueOf(docenteDesignacion.getHorasSemanales()), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(docenteDesignacion.getCargo(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(docenteDesignacion.getDesignacion(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(String.valueOf(docenteDesignacion.getHorasDesignacion()), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(dictado, new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase( "", new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase( "", new Font(Font.HELVETICA, 8)));
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

            Paragraph titleParagraph = new Paragraph("Designaciones por Docentes", new Font(Font.HELVETICA, 13, Font.BOLD));
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
