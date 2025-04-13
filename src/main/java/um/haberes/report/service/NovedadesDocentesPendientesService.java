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
import um.haberes.report.client.haberes.core.*;
import um.haberes.report.kotlin.dto.haberes.core.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NovedadesDocentesPendientesService {
    private Environment environment;
    private CursoClient cursoClient;
    private GeograficaClient geograficaClient;
    private FacultadClient facultadClient;
    private CursoCargoNovedadClient cursoCargoNovedadClient;
    private PersonaClient personaClient;
    private CargoTipoClient cargoTipoClient;

    public NovedadesDocentesPendientesService(Environment environment, CursoClient cursoClient, GeograficaClient geograficaClient, FacultadClient facultadClient, CursoCargoNovedadClient cursoCargoNovedadClient, PersonaClient personaClient, CargoTipoClient cargoTipoClient) {
        this.environment = environment;
        this.cursoClient = cursoClient;
        this.geograficaClient = geograficaClient;
        this.facultadClient = facultadClient;
        this.cursoCargoNovedadClient = cursoCargoNovedadClient;
        this.personaClient = personaClient;
        this.cargoTipoClient = cargoTipoClient;
    }

    public String generate(Integer anho, Integer mes, Integer autorizado, Integer rechazado) {
        String path = environment.getProperty("path.files");
        String filename = path + "novedadesDocentesPendientes." + anho + "." + mes + "." + autorizado + "." + rechazado + ".pdf";

        generateReport(filename, anho, mes, autorizado, rechazado);
        return filename;
    }

    private void generateReport(String filename, Integer anho, Integer mes, Integer autorizado, Integer rechazado) {
        log.debug("Generating report for anho {}, mes {}, autorizado {}, rechazado {}", anho, mes, autorizado, rechazado);
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            NovedadesDocentesPendientesService.HeaderFooter event = new NovedadesDocentesPendientesService.HeaderFooter(anho, mes, autorizado, rechazado);
            writer.setPageEvent(event);
            document.open();

            // Encabezado del documento
            log.debug("Setting header");
            event.setHeader(document);

            // Busca las novedades según los criterios
            log.debug("Finding novedades");
            List<CursoCargoNovedadDto> allNovedades = new ArrayList<>();
            if (autorizado == 1 && rechazado == 0) {
                allNovedades.addAll(cursoCargoNovedadClient.findAllAutorizadosAlta(anho, mes));
                allNovedades.addAll(cursoCargoNovedadClient.findAllAutorizadosBaja(anho, mes));
            } else if (autorizado == 0 && rechazado == 1) {
                allNovedades.addAll(cursoCargoNovedadClient.findAllRechazadosAlta(anho, mes));
                allNovedades.addAll(cursoCargoNovedadClient.findAllRechazadosBaja(anho, mes));
            } else if (autorizado == 0 && rechazado == 0) {
                allNovedades.addAll(cursoCargoNovedadClient.findAllPendientesAlta(anho, mes));
                allNovedades.addAll(cursoCargoNovedadClient.findAllPendientesBaja(anho, mes));
            } else {
                allNovedades.addAll(cursoCargoNovedadClient.findAllPendientes(anho, mes));
            }

            if (allNovedades.isEmpty()) {
                // Solo mostramos una estructura vacía
                PdfPTable facultadTable = new PdfPTable(1);
                facultadTable.setWidthPercentage(100);
                Paragraph fPar = new Paragraph();
                fPar.add(new Phrase("Facultad: ", new Font(Font.HELVETICA, 11)));
                facultadTable.addCell(createCell(fPar, Rectangle.NO_BORDER));
                document.add(facultadTable);

                PdfPTable sedeTable = new PdfPTable(1);
                sedeTable.setWidthPercentage(100);
                Paragraph sPar = new Paragraph();
                sPar.add(new Phrase("Sede: ", new Font(Font.HELVETICA, 11)));
                sedeTable.addCell(createCell(sPar, Rectangle.NO_BORDER));
                document.add(sedeTable);

                PdfPTable cursoTable = new PdfPTable(1);
                cursoTable.setWidthPercentage(100);
                Paragraph cPar = new Paragraph();
                cPar.add(new Phrase("Curso: ", new Font(Font.HELVETICA, 10)));
                cursoTable.addCell(createCell(cPar, Rectangle.NO_BORDER));
                document.add(cursoTable);

                float[] widths = {25, 100, 15, 20, 20, 20};
                PdfPTable cargoTable = new PdfPTable(widths);
                cargoTable.setWidthPercentage(95);

                String[] headers = {"Cargo", "Docente", "Horas", "Desarraigo", "Aut.", "Rech."};
                for (String h : headers) {
                    cargoTable.addCell(createCell(h, Element.ALIGN_CENTER, true));
                }

                PdfPCell empty = new PdfPCell(new Phrase("Sin novedades docentes para mostrar", new Font(Font.HELVETICA, 8, Font.ITALIC)));
                empty.setColspan(6);
                empty.setBorder(Rectangle.NO_BORDER);
                empty.setHorizontalAlignment(Element.ALIGN_CENTER);
                cargoTable.addCell(empty);

                document.add(cargoTable);
                document.add(Chunk.NEWLINE);
                document.close();
                return;
            }

            List<FacultadDto> todasLasFacultades = facultadClient.findAll(); // Trae todas las facultades

            for (FacultadDto facultad : todasLasFacultades) {
                Integer facultadId = facultad.getFacultadId();

                List<CursoCargoNovedadDto> novedadesFacultad = allNovedades.stream()
                        .filter(n -> n.getCurso().getFacultadId().equals(facultadId))
                        .toList();

                if (novedadesFacultad.isEmpty()) {
                    continue; // NO imprimimos nada si no hay info
                }

                // ✅ Ahora SÍ imprimimos la Facultad, porque hay info
                PdfPTable facultadTable = new PdfPTable(1);
                facultadTable.setWidthPercentage(100);
                Paragraph fPar = new Paragraph();
                fPar.add(new Phrase("Facultad: ", new Font(Font.HELVETICA, 11)));
                fPar.add(new Phrase(facultad.getNombre(), new Font(Font.HELVETICA, 11, Font.BOLD)));
                facultadTable.addCell(createCell(fPar, Rectangle.NO_BORDER));
                document.add(facultadTable);

                Map<Integer, List<CursoCargoNovedadDto>> novedadesPorGeografica = novedadesFacultad.stream()
                        .collect(Collectors.groupingBy(n -> n.getCurso().getGeograficaId()));

                for (Integer geograficaId : novedadesPorGeografica.keySet()) {
                    List<CursoCargoNovedadDto> novedadesSede = novedadesPorGeografica.get(geograficaId);
                    if (novedadesSede == null || novedadesSede.isEmpty()) {
                        continue;
                    }

                    GeograficaDto geografica = geograficaClient.findByGeograficaId(geograficaId);

                    PdfPTable sedeTable = new PdfPTable(1);
                    sedeTable.setWidthPercentage(100);
                    Paragraph sPar = new Paragraph();
                    sPar.add(new Phrase("Sede: ", new Font(Font.HELVETICA, 11)));
                    sPar.add(new Phrase(geografica.getNombre(), new Font(Font.HELVETICA, 11, Font.BOLD)));
                    sedeTable.addCell(createCell(sPar, Rectangle.NO_BORDER));
                    document.add(sedeTable);

                    Map<Long, List<CursoCargoNovedadDto>> novedadesPorCurso = novedadesSede.stream()
                            .collect(Collectors.groupingBy(n -> n.getCurso().getCursoId()));

                    for (Map.Entry<Long, List<CursoCargoNovedadDto>> entry : novedadesPorCurso.entrySet()) {
                        Long cursoId = entry.getKey();
                        List<CursoCargoNovedadDto> novedades = entry.getValue();
                        CursoDto curso = cursoClient.findByCursoId(cursoId);

                        PdfPTable cursoTable = new PdfPTable(1);
                        cursoTable.setWidthPercentage(100);
                        Paragraph cPar = new Paragraph();
                        cPar.add(new Phrase("Curso: ", new Font(Font.HELVETICA, 10)));
                        cPar.add(new Phrase(curso.getNombre(), new Font(Font.HELVETICA, 10, Font.BOLD)));
                        cursoTable.addCell(createCell(cPar, Rectangle.NO_BORDER));
                        document.add(cursoTable);

                        float[] widths = {25, 100, 15, 20, 20, 20};
                        PdfPTable cargoTable = new PdfPTable(widths);
                        cargoTable.setWidthPercentage(95);

                        String[] headers = {"Cargo", "Docente", "Horas", "Desarraigo", "Aut.", "Rech."};
                        for (String h : headers) {
                            cargoTable.addCell(createCell(h, Element.ALIGN_CENTER, true));
                        }

                        if (novedades.isEmpty()) {
                            PdfPCell empty = new PdfPCell(new Phrase("Sin novedades", new Font(Font.HELVETICA, 8, Font.ITALIC)));
                            empty.setColspan(6);
                            empty.setBorder(Rectangle.NO_BORDER);
                            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cargoTable.addCell(empty);
                        } else {
                            for (CursoCargoNovedadDto nov : novedades) {
                                CargoTipoDto tipo = cargoTipoClient.findByCargoTipoId(nov.getCargoTipoId());
                                PersonaDto persona = personaClient.findByLegajoId(nov.getLegajoId());
                                addNovedadDocentesPEndientesDetails(document, nov, curso, tipo, persona);

                                if (nov.getSolicitud() != null && !nov.getSolicitud().isBlank()) {
                                    PdfPTable detailTable = new PdfPTable(1);
                                    detailTable.setWidthPercentage(85);
                                    Paragraph obs = new Paragraph();
                                    obs.add(new Phrase("Observaciones: ", new Font(Font.HELVETICA, 10, Font.BOLD)));
                                    obs.add(new Phrase(nov.getSolicitud(), new Font(Font.HELVETICA, 10)));
                                    detailTable.addCell(createCell(obs, Rectangle.NO_BORDER));
                                    document.add(detailTable);
                                }
                            }
                        }

                        document.add(cargoTable);
                        document.add(Chunk.NEWLINE);
                    }
                }
            }
            document.close();
        } catch (DocumentException | IOException e) {
            log.debug("Error generating report {}", e.getMessage());
        }
    }

    // Helpers
    private PdfPCell createCell(String text, int align) {
        return createCell(text, align, false);
    }

    private PdfPCell createCell(String text, int align, boolean bold) {
        Font font = new Font(Font.HELVETICA, 8, bold ? Font.BOLD : Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(align);
        return cell;
    }

    private PdfPCell createCell(Paragraph content, int border) {
        PdfPCell cell = new PdfPCell(content);
        cell.setBorder(border);
        return cell;
    }


    private void addNovedadDocentesPEndientesDetails(Document document, CursoCargoNovedadDto novedad, CursoDto curso, CargoTipoDto cargoTipo, PersonaDto persona) throws DocumentException {
        try {
            log.debug("Adding novedad {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(novedad));
        } catch (JsonProcessingException e) {
            log.debug("Error serializing novedad {}", e.getMessage());
        }
        PdfPTable detailTable = new PdfPTable(1);
        detailTable.setWidthPercentage(100);

        // Encabezado del curso
        var paragraph = new Paragraph();
        var cell = new PdfPCell(paragraph);

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
        paragraph.add(new Phrase(cargoTipo.getNombre(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cargoTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(persona.getApellidoNombre(), new Font(Font.HELVETICA, 8, Font.BOLD)));
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

        document.add(detailTable);
//        document.add(Chunk.NEWLINE);
    }

    static class HeaderFooter extends PdfPageEventHelper {
        private final Integer anho;
        private final Integer mes;
        private final Integer autorizado;
        private final Integer rechazado;
        private PdfPTable headerTable;

        public HeaderFooter(Integer anho, Integer mes, Integer autorizado, Integer rechazado) {
            this.anho = anho;
            this.mes = mes;
            this.autorizado = autorizado;
            this.rechazado = rechazado;
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
            paragraph.add(new Phrase("\n\nNovedades Docentes Pendientes", new Font(Font.HELVETICA, 13, Font.BOLD)));
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