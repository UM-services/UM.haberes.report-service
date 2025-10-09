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
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CursoDocenteService {

    private final Environment environment;
    private final CursoCargoClient cursoCargoClient;
    private final PersonaClient personaClient;
    private final CursoClient cursoClient;
    private final CategoriaClient categoriaClient;
    private final FacultadClient facultadClient;
    private final GeograficaClient geograficaClient;

    public String generate(Integer anho, Integer mes) {
        String path = environment.getProperty("path.files");
        String filename = path + "curso-docente." + "." + anho + "." + mes + ".pdf";

        generateReport(filename, anho, mes);
        return filename;
    }

    private void generateReport(String filename, Integer anho, Integer mes) {
        log.debug("Generating report for anho {}, mes {}", anho, mes);
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            log.debug("writer created");
            CursoDocenteService.HeaderFooter event = new CursoDocenteService.HeaderFooter(anho, mes);
            log.debug("event created");
            writer.setPageEvent(event);
            log.debug("page event set");
            document.open();
            log.debug("document opened");

            // Encabezado del documento
            event.setHeader(document);
            log.debug("header set");

            List<PersonaDto> personas = personaClient.findAll();
            if (personas == null || personas.isEmpty()) {
                log.warn("No se encontraron personas");
                return;
            }

            // Filtrar y ordenar docentes con cursos
            List<PersonaDto> docentesConCursos = personas.stream()
                    .filter(p -> p.getLegajoId() != null)
                    .collect(Collectors.toList());

            // Obtener cursos en una sola consulta
            Map<Long, List<CursoCargoDto>> cursosPorLegajo = docentesConCursos.stream()
                    .collect(Collectors.toMap(
                            PersonaDto::getLegajoId,
                            p -> cursoCargoClient.findAllByLegajo(p.getLegajoId(), anho, mes),
                            (a, b) -> a
                    ));

            // Filtrar solo docentes con cursos
            docentesConCursos = docentesConCursos.stream()
                    .filter(p -> cursosPorLegajo.containsKey(p.getLegajoId()) && !cursosPorLegajo.get(p.getLegajoId()).isEmpty())
                    .sorted(Comparator.comparing(PersonaDto::getApellido)) // Orden por apellido
                    .collect(Collectors.toList());

            if (docentesConCursos.isEmpty()) {
                log.warn("No se encontraron docentes con cursos");
                return;
            }

            // Obtener listas de IDs únicos
            Set<Long> cursoIds = cursosPorLegajo.values().stream()
                    .flatMap(List::stream)
                    .map(CursoCargoDto::getCursoId)
                    .filter(Objects::nonNull) // Filtrar valores nulos
                    .collect(Collectors.toSet());

            Set<Integer> categoriaIds = cursosPorLegajo.values().stream()
                    .flatMap(List::stream)
                    .map(CursoCargoDto::getCategoriaId)
                    .collect(Collectors.toSet()); // No filtrar null aquí

            // Obtener datos en una sola consulta
            Map<Long, CursoDto> cursosMap = cursoClient.findAll().stream()
                    .filter(Objects::nonNull) // Asegurar que curso no sea null
                    .filter(curso -> cursoIds.contains(curso.getCursoId()))
                    .collect(Collectors.toMap(CursoDto::getCursoId, c -> c));

            Map<Integer, CategoriaDto> categoriasMap = categoriaClient.findAll().stream()
                    .filter(Objects::nonNull) // Asegurar que categoria no sea null
                    .filter(categoria -> categoriaIds.contains(categoria.getCategoriaId()))
                    .collect(Collectors.toMap(CategoriaDto::getCategoriaId, c -> c));

            // Agregar una categoría vacía para manejar valores nulos
            CategoriaDto categoriaVacia = new CategoriaDto();
            categoriaVacia.setNombre("");
            categoriasMap.put(null, categoriaVacia); // Permite manejar los valores null en el Map

            // Obtener IDs de facultades y geografías
            Set<Integer> facultadIds = cursosMap.values().stream().map(CursoDto::getFacultadId).collect(Collectors.toSet());
            Set<Integer> geograficaIds = cursosMap.values().stream().map(CursoDto::getGeograficaId).collect(Collectors.toSet());

        // Obtener facultades y geografías en una sola consulta
            Map<Integer, FacultadDto> facultadesMap = facultadClient.findAllFacultades().stream()
                    .filter(facultad -> facultadIds.contains(facultad.getFacultadId()))
                    .collect(Collectors.toMap(FacultadDto::getFacultadId, f -> f));

            Map<Integer, GeograficaDto> geograficaMap = geograficaClient.findAllByGeograficaIdIn(new ArrayList<>(geograficaIds)).stream()
                    .collect(Collectors.toMap(GeograficaDto::getGeograficaId, g -> g));

            for (PersonaDto persona : docentesConCursos) {
                Long legajoId = persona.getLegajoId();

                Paragraph docenteTitle = new Paragraph();
                docenteTitle.add(new Phrase(legajoId + ". ", new Font(Font.HELVETICA, 9, Font.BOLD)));
                docenteTitle.add(new Phrase(persona.getApellido() + ", " + persona.getNombre(), new Font(Font.HELVETICA, 9, Font.BOLD)));
                docenteTitle.setSpacingBefore(10);
                docenteTitle.setSpacingAfter(5);
                document.add(docenteTitle);

                PdfPTable detailTable = new PdfPTable(9);
                float[] columnWidths = {30, 17, 43, 10, 20, 15, 10, 10, 10};
                detailTable.setWidthPercentage(95);
                detailTable.setWidths(columnWidths);

                addTableHeader(detailTable);

                List<CursoCargoDto> cursosCargo = cursosPorLegajo.get(legajoId);

                for (CursoCargoDto cursoCargo : cursosCargo) {
                    CursoDto curso = cursosMap.get(cursoCargo.getCursoId());
                    if (curso == null) {
                        log.warn("Curso con ID {} no encontrado", cursoCargo.getCursoId());
                        continue; // Evita procesar cursos nulos
                    }

                    Integer categoriaId = cursoCargo.getCategoriaId();
                    CategoriaDto categoria = categoriasMap.getOrDefault(categoriaId, categoriaVacia); // Si es null, usa la vacía

                    FacultadDto facultad = facultadesMap.get(curso.getFacultadId());
                    if (facultad == null) {
                        log.warn("Facultad con ID {} no encontrada", curso.getFacultadId());
                    }

                    GeograficaDto geografica = geograficaMap.get(curso.getGeograficaId());
                    if (geografica == null) {
                        log.warn("Geográfica con ID {} no encontrada", curso.getGeograficaId());
                    }

                    addCursoDocenteDetails(document, detailTable, facultad, cursoCargo, persona, curso, categoria, geografica);
                }

                document.add(detailTable);
            }

            document.close();
        } catch (Exception e) {
            log.debug("Error generating report {}", e.getMessage());
        }
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

        cell = new PdfPCell(new Phrase("Básico", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Anual", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Sem 1", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Sem 2", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }

    private void addCursoDocenteDetails(Document document, PdfPTable detailTable, FacultadDto facultad, CursoCargoDto cursoCargo, PersonaDto persona, CursoDto curso, CategoriaDto categoria, GeograficaDto geografica) throws DocumentException {

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "ES"));

        // Asegurar que los valores nunca sean null
        //BigDecimal totalRemunerativoActual = actual.getTotalRemunerativo() != null ? actual.getTotalRemunerativo() : BigDecimal.ZERO;


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
        paragraph.add(new Phrase(curso.getNombre(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(cursoCargo.getHorasSemanales().toString(), new Font(Font.HELVETICA, 8)));
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
        paragraph.add(new Phrase(categoria.getBasico().toString(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(curso.getAnual() == 1 ? "1" : "", new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(curso.getSemestre1() == 1 ? "1" : "", new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(curso.getSemestre2() == 1 ? "1" : "", new Font(Font.HELVETICA, 8)));
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

            Paragraph titleParagraph = new Paragraph("Cursos por Docente", new Font(Font.HELVETICA, 13, Font.BOLD));
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






