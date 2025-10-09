/**
 *
 */
package um.haberes.report.service;

import org.openpdf.text.*;
import org.openpdf.text.pdf.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import um.haberes.report.client.haberes.core.*;
import um.haberes.report.kotlin.dto.haberes.core.*;
import um.haberes.report.util.Tool;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author daniel
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BonoService {

    static final Integer const_Nivel_Grado = 1;
    static final Integer const_Nivel_Secundario = 2;
    static final Integer const_Nivel_Tecnicatura = 3;

    private final Environment environment;
    private final PersonaClient personaClient;
    private final FacultadClient facultadClient;
    private final CursoCargoClient cursoCargoClient;
    private final ControlClient controlClient;
    private final LiquidacionClient liquidacionClient;
    private final AntiguedadClient antiguedadClient;
    private final DesignacionToolClient designacionToolClient;
    private final CargoLiquidacionClient cargoLiquidacionClient;
    private final CargoClaseDetalleClient cargoClaseDetalleClient;
    private final LegajoBancoClient legajoBancoClient;
    private final DependenciaClient dependenciaClient;
    private final ItemClient itemClient;
    private final LiquidacionEtecClient liquidacionEtecClient;
    private final LiquidacionAdicionalClient liquidacionAdicionalClient;
    private final CodigoClient codigoClient;
    private final CodigoGrupoClient codigoGrupoClient;
    private final LetraClient letraClient;
    private final BonoImpresionClient bonoImpresionClient;
    private final JavaMailSender javaMailSender;
    private final ContactoClient contactoClient;
    private final LegajoControlClient legajoControlClient;

    public String generatePdfDependencia(Integer anho, Integer mes, Integer dependenciaId, String salida,
                                         Long legajoIdSolicitud, String ipAddress) {
        String path = environment.getProperty("path.files");
        ControlDto control = controlClient.findByPeriodo(anho, mes);

        String filename = "";
        List<String> filenames = new ArrayList<>();
        for (LiquidacionDto liquidacion : liquidacionClient.findAllByDependencia(dependenciaId, anho, mes, salida)) {
            filenames.add(filename = path + "bono." + liquidacion.getLegajoId() + "." + anho + "." + mes + ".pdf");
            log.debug("Filename -> {}", filename);
            filename = makePdfConFusion(filename, liquidacion.getLegajoId(), anho, mes, legajoIdSolicitud, ipAddress, control);
        }

        try {
            mergePdf(filename = path + "dependencia." + dependenciaId + ".pdf", filenames);
        } catch (IOException e) {
            log.error("Error al generar el archivo PDF: {}", e.getMessage());
        }

        return filename;
    }

    private void mergePdf(String filename, List<String> filenames) throws IOException {
        OutputStream outputStream = new FileOutputStream(filename);
        Document document = new Document();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
        for (String name : filenames) {
            PdfReader pdfReader = new PdfReader(new FileInputStream(name));
            for (int pagina = 0; pagina < pdfReader.getNumberOfPages(); ) {
                document.newPage();
                PdfImportedPage page = pdfWriter.getImportedPage(pdfReader, ++pagina);
                pdfContentByte.addTemplate(page, 0, 0);
            }
        }
        outputStream.flush();
        document.close();
        outputStream.close();
    }

    public String generateDetalleCargosPdf(Long legajoId, Integer anho, Integer mes, Integer facultadId) {
        String path = environment.getProperty("path.files");
        String filename = path + "cargos." + legajoId + "." + anho + "." + mes + "." + facultadId + ".pdf";

        return makePdfCargos(filename, legajoId, anho, mes, facultadId);
    }

    private String makePdfCargos(String filename, Long legajoId, Integer anho, Integer mes, Integer facultadId) {
        PersonaDto persona = personaClient.findByLegajoId(legajoId);
        FacultadDto facultad = facultadClient.findByFacultadId(facultadId);
        AntiguedadDto antiguedad = antiguedadClient.findByUnique(legajoId, anho, mes);
        int mesesAntiguedad = Math.max(antiguedad.getMesesDocentes(), antiguedad.getMesesAdministrativos());
        List<BigDecimal> indices = designacionToolClient.indiceAntiguedad(legajoId, anho, mes);
        log.debug("Indices={}", indices);

        try {
            Document document = new Document(new Rectangle(PageSize.A4));
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.setMargins(20, 20, 20, 20);
            document.open();

            Image marca = Image.getInstance("marca_um.png");

            // Tabla logo y datos UM
            float[] columnHeader = {2, 8};
            PdfPTable tableHeader = new PdfPTable(columnHeader);
            tableHeader.setWidthPercentage(100);

            PdfPCell cell = new PdfPCell(marca);
            cell.setBorder(Rectangle.NO_BORDER);
            tableHeader.addCell(cell);

            Paragraph paragraph = new Paragraph();
            paragraph.add(new Phrase("Universidad de Mendoza", new Font(Font.HELVETICA, 16, Font.BOLD)));
            paragraph.add(new Phrase("\n" + facultad.getNombre(), new Font(Font.HELVETICA, 11, Font.BOLD)));
            cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setLeading(0, 1.5f);
            tableHeader.addCell(cell);
            document.add(tableHeader);

            // Centrado Título
            paragraph = new Paragraph("Detalle de Cargos", new Font(Font.HELVETICA, 12, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setMultipliedLeading(2f);
            document.add(paragraph);
            // Nombre, documento y cuil
            paragraph = new Paragraph(new Phrase("Apellido, Nombre: ", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase(MessageFormat.format("{0}, {1}", persona.getApellido(), persona.getNombre()),
                    new Font(Font.HELVETICA, 9, Font.BOLD)));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setMultipliedLeading(2f);
            document.add(paragraph);

            paragraph = new Paragraph(new Phrase("Legajo: ", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase(Objects.requireNonNull(persona.getLegajoId()).toString(), new Font(Font.HELVETICA, 9, Font.BOLD)));
            paragraph.add(new Phrase("         Antigüedad: ", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase(MessageFormat.format("{0}.{1}", mesesAntiguedad / 12, mesesAntiguedad % 12),
                    new Font(Font.HELVETICA, 9, Font.BOLD)));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setMultipliedLeading(2f);
            document.add(paragraph);
            // Cursos
            List<CursoCargoDto> cursos = cursoCargoClient.findAllByFacultad(legajoId, anho, mes, facultadId);
            if (!cursos.isEmpty()) {
                float[] columnCurso = {4, 24, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f};
                PdfPTable tableCurso = new PdfPTable(columnCurso);
                tableCurso.setWidthPercentage(90);
                tableCurso.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(new Paragraph("Cargo", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tableCurso.addCell(cell);
                cell = new PdfPCell(new Paragraph("Curso", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableCurso.addCell(cell);
                cell = new PdfPCell(new Paragraph("Ds", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCurso.addCell(cell);
                cell = new PdfPCell(new Paragraph("An", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCurso.addCell(cell);
                cell = new PdfPCell(new Paragraph("S1", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCurso.addCell(cell);
                cell = new PdfPCell(new Paragraph("S2", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCurso.addCell(cell);
                cell = new PdfPCell(new Paragraph("Hr", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCurso.addCell(cell);
                for (CursoCargoDto cursoCargo : cursos) {
                    cell = new PdfPCell(new Paragraph(Objects.requireNonNull(cursoCargo.getCargoTipo()).getNombre(),
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCurso.addCell(cell);
                    cell = new PdfPCell(new Paragraph(Objects.requireNonNull(cursoCargo.getCurso()).getNombre(), new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCurso.addCell(cell);
                    cell = new PdfPCell(new Paragraph(cursoCargo.getDesarraigo() == 1 ? "*" : "",
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableCurso.addCell(cell);
                    cell = new PdfPCell(new Paragraph(cursoCargo.getCurso().getAnual() == 1 ? "*" : "",
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableCurso.addCell(cell);
                    cell = new PdfPCell(new Paragraph(cursoCargo.getCurso().getSemestre1() == 1 ? "*" : "",
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableCurso.addCell(cell);
                    cell = new PdfPCell(new Paragraph(cursoCargo.getCurso().getSemestre2() == 1 ? "*" : "",
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableCurso.addCell(cell);
                    cell = new PdfPCell(new Paragraph(new DecimalFormat("#0").format(cursoCargo.getHorasSemanales()),
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableCurso.addCell(cell);
                }
                document.add(tableCurso);
            }
            // Actividad Docente
            List<CargoLiquidacionDto> cargos = cargoLiquidacionClient.findAllDocenteByLegajoAndFacultad(legajoId, anho, mes, facultadId);
            if (!cargos.isEmpty()) {
                paragraph = new Paragraph("Actividad Docente", new Font(Font.HELVETICA, 8, Font.BOLD));
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.setMultipliedLeading(2f);
                document.add(paragraph);
                // Cargos Liquidados
                float[] columnGrupo = {1, 1};
                PdfPTable tableGrupo = new PdfPTable(columnGrupo);
                tableGrupo.setWidthPercentage(90);
                tableGrupo.setHorizontalAlignment(Element.ALIGN_CENTER);
                float[] columnCargo = {2, 6, 3};
                PdfPTable tableCargo = new PdfPTable(columnCargo);
                tableCargo.setWidthPercentage(100);
                tableCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(new Paragraph("Dep", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(new Paragraph("Cargo", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(new Paragraph("Básico", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(tableCargo);
                cell.setBorder(Rectangle.NO_BORDER);
                tableGrupo.addCell(cell);
                tableGrupo.addCell(cell);
                document.add(tableGrupo);
                tableGrupo = new PdfPTable(columnGrupo);
                tableGrupo.setWidthPercentage(90);
                tableGrupo.setHorizontalAlignment(Element.ALIGN_CENTER);
                int count = 0;
                for (CargoLiquidacionDto cargoLiquidacion : cargos) {
                    count++;
                    tableCargo = new PdfPTable(columnCargo);
                    tableCargo.setWidthPercentage(100);
                    tableCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(new Paragraph(Objects.requireNonNull(cargoLiquidacion.getDependencia()).getAcronimo(),
                            new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(
                            new Paragraph(Objects.requireNonNull(cargoLiquidacion.getCategoria()).getNombre(), new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(
                            new Paragraph(new DecimalFormat("#,###.00").format(cargoLiquidacion.getCategoriaBasico()),
                                    new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(tableCargo);
                    cell.setBorder(Rectangle.NO_BORDER);
                    tableGrupo.addCell(cell);
                }
                // Completa con una celda vacía si la cantidad de cargos es impar
                if (count % 2 != 0) {
                    cell = new PdfPCell();
                    cell.setBorder(Rectangle.NO_BORDER);
                    tableGrupo.addCell(cell);
                }
                document.add(tableGrupo);
            }
            // Actividad No Docente
            cargos = cargoLiquidacionClient.findAllNoDocenteByLegajoAndFacultad(legajoId, anho, mes, facultadId);
            if (!cargos.isEmpty()) {
                paragraph = new Paragraph("Actividad No Docente", new Font(Font.HELVETICA, 8, Font.BOLD));
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.setMultipliedLeading(2f);
                document.add(paragraph);
                // Cargos Liquidados
                float[] columnGrupo = {1, 1};
                PdfPTable tableGrupo = new PdfPTable(columnGrupo);
                tableGrupo.setWidthPercentage(90);
                tableGrupo.setHorizontalAlignment(Element.ALIGN_CENTER);
                float[] columnCargo = {2, 6, 3};
                PdfPTable tableCargo = new PdfPTable(columnCargo);
                tableCargo.setWidthPercentage(100);
                tableCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(new Paragraph("Dep", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(new Paragraph("Cargo", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(new Paragraph("Básico", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(tableCargo);
                cell.setBorder(Rectangle.NO_BORDER);
                tableGrupo.addCell(cell);
                tableGrupo.addCell(cell);
                document.add(tableGrupo);
                tableGrupo = new PdfPTable(columnGrupo);
                tableGrupo.setWidthPercentage(90);
                tableGrupo.setHorizontalAlignment(Element.ALIGN_CENTER);
                int count = 0;
                for (CargoLiquidacionDto cargoLiquidacion : cargos) {
                    count++;
                    tableCargo = new PdfPTable(columnCargo);
                    tableCargo.setWidthPercentage(100);
                    tableCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(new Paragraph(Objects.requireNonNull(cargoLiquidacion.getDependencia()).getAcronimo(),
                            new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(
                            new Paragraph(Objects.requireNonNull(cargoLiquidacion.getCategoria()).getNombre(), new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCargo.addCell(cell);
                    BigDecimal multiplicador = BigDecimal.ONE;
                    if (cargoLiquidacion.getHorasJornada().compareTo(BigDecimal.ZERO) == 0) {
                        multiplicador = new BigDecimal(cargoLiquidacion.getJornada());
                    } else {
                        multiplicador = cargoLiquidacion.getHorasJornada();
                    }
                    cell = new PdfPCell(new Paragraph(
                            new DecimalFormat("#,###.00").format(cargoLiquidacion.getCategoriaBasico()
                                    .multiply(multiplicador).setScale(2, RoundingMode.HALF_UP)),
                            new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(tableCargo);
                    cell.setBorder(Rectangle.NO_BORDER);
                    tableGrupo.addCell(cell);
                }
                // Completa con una celda vacía si la cantidad de cargos es impar
                if (count % 2 != 0) {
                    cell = new PdfPCell();
                    cell.setBorder(Rectangle.NO_BORDER);
                    tableGrupo.addCell(cell);
                }
                document.add(tableGrupo);
            }
            // Cargos con Clase
            List<CargoClaseDetalleDto> clases = cargoClaseDetalleClient.findAllByLegajoAndFacultad(legajoId, anho, mes, facultadId);
            if (!clases.isEmpty()) {
                paragraph = new Paragraph("Actividad Académica", new Font(Font.HELVETICA, 8, Font.BOLD));
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.setMultipliedLeading(2f);
                document.add(paragraph);
                float[] columnCargo = {2, 14, 3};
                PdfPTable tableCargo = new PdfPTable(columnCargo);
                tableCargo.setWidthPercentage(90);
                tableCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(new Paragraph("Dep", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(new Paragraph("Cargo", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(new Paragraph("Básico", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tableCargo.addCell(cell);
                for (CargoClaseDetalleDto cargoClaseDetalle : clases) {
                    cell = new PdfPCell(new Paragraph(Objects.requireNonNull(cargoClaseDetalle.getDependencia()).getAcronimo(),
                            new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(new Paragraph(
                            MessageFormat.format("{0} / {1} / {2} / {3} / {4} {5}",
                                    Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(cargoClaseDetalle.getCargoClase()).getClase())).getNombre(),
                                    cargoClaseDetalle.getCargoClase().getNombre(),
                                    Objects.requireNonNull(cargoClaseDetalle.getCargoClasePeriodo()).getDescripcion(),
                                    Objects.requireNonNull(cargoClaseDetalle.getCargoClasePeriodo().getGeografica()).getNombre(),
                                    cargoClaseDetalle.getCargoClasePeriodo().getHoras(), "horas"),
                            new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(
                            new Paragraph(new DecimalFormat("#,###.00").format(
                                    cargoClaseDetalle.getValorHora()
                                            .multiply(new BigDecimal(cargoClaseDetalle.getHoras()))),
                                    new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCargo.addCell(cell);
                }
                document.add(tableCargo);
            }
            document.close();
        } catch (BadElementException | IOException e) {
            log.debug("Error al generar el PDF", e);
        }

        return filename;
    }

    public String generatePdf(Long legajoId, Integer anho, Integer mes, Long legajoIdSolicitud, String ipAddress) {
        String path = environment.getProperty("path.files");
        String filename = path + "bono." + legajoId + "." + anho + "." + mes + ".pdf";
        ControlDto control = controlClient.findByPeriodo(anho, mes);

        return makePdfConFusion(filename, legajoId, anho, mes, legajoIdSolicitud, ipAddress, control);
    }

    public String makePdfConFusion(String filename, Long legajoId, Integer anho, Integer mes, Long legajoIdSolicitud,
                                   String ipAddress, ControlDto control) {
        PersonaDto persona = personaClient.findByLegajoId(legajoId);
        AntiguedadDto antiguedad = antiguedadClient.findByUnique(legajoId, anho, mes);
        int mesesAntiguedad = Math.max(antiguedad.getMesesDocentes(), antiguedad.getMesesAdministrativos());
        List<BigDecimal> indices = designacionToolClient.indiceAntiguedad(legajoId, anho, mes);
        log.debug("Indices={}", indices);
        LegajoBancoDto legajoBanco = null;
        try {
            legajoBanco = legajoBancoClient.findLegajoCbuPrincipal(legajoId, anho, mes);
        } catch (Exception e) {
            legajoBanco = new LegajoBancoDto();
        }
        log.debug("LegajoBanco -> {}", legajoBanco);
        Map<Integer, DependenciaDto> dependencias = dependenciaClient.findAll().stream()
                .collect(Collectors.toMap(DependenciaDto::getDependenciaId, dependencia -> dependencia));
        // Elimina los items con importe 0 que no sean de los subtotales
        itemClient.deleteAllByZero(legajoId, anho, mes);

        Map<Integer, ItemDto> items = itemClient.findAllByLegajo(legajoId, anho, mes).stream()
                .collect(Collectors.toMap(ItemDto::getCodigoId, Function.identity(), (item, replacement) -> item));

        try {
            Document document = new Document(new Rectangle(PageSize.A4));
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.setMargins(20, 20, 20, 20);
            document.open();

            Image marca = Image.getInstance("marca_um.png");
            Image firma = Image.getInstance("firma_new.png");

            // Tabla logo y datos UM
            float[] columnHeader = {2, 8};
            PdfPTable tableHeader = new PdfPTable(columnHeader);
            tableHeader.setWidthPercentage(100);

            PdfPCell cell = new PdfPCell(marca);
            cell.setBorder(Rectangle.NO_BORDER);
            tableHeader.addCell(cell);

            Paragraph paragraph = new Paragraph(
                    new Phrase("Universidad de Mendoza", new Font(Font.HELVETICA, 16, Font.BOLD)));
            paragraph.add(new Phrase("\nBoulogne Sur Mer 683 - 5500 - Mendoza", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase("\nRepública Argentina", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase("\nCUIT: 30-51859446-6", new Font(Font.HELVETICA, 8)));
            cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setLeading(0, 1.5f);
            tableHeader.addCell(cell);
            document.add(tableHeader);

            // Centrado Título
            paragraph = new Paragraph("Recibo por Pago de Remuneraciones", new Font(Font.HELVETICA, 12, Font.BOLD));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setMultipliedLeading(2f);
            document.add(paragraph);
            // Nombre, documento y cuil
            paragraph = new Paragraph(new Phrase("Apellido, Nombre: ", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase(MessageFormat.format("{0}, {1}", persona.getApellido(), persona.getNombre()),
                    new Font(Font.HELVETICA, 9, Font.BOLD)));
            paragraph.add(new Phrase("                   DU: ", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase(persona.getDocumento().toString(), new Font(Font.HELVETICA, 9, Font.BOLD)));
            paragraph.add(new Phrase("                   CUIL: ", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase(persona.getCuil(), new Font(Font.HELVETICA, 9, Font.BOLD)));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setMultipliedLeading(2f);
            document.add(paragraph);
            // Legajo, dependencia y básico
            DependenciaDto dependencia = dependencias.get(persona.getDependenciaId());
            ItemDto item = null;
            BigDecimal horasETEC = BigDecimal.ZERO;
            BigDecimal basicoETEC = BigDecimal.ZERO;
            BigDecimal presentismoETEC = BigDecimal.ZERO;
            BigDecimal antiguedadETEC = BigDecimal.ZERO;
            BigDecimal adicionalETEC = BigDecimal.ZERO;
            BigDecimal porcentajeAntiguedadETEC = liquidacionEtecClient.calcularPorcentajeAntiguedad(legajoId, anho, mes, 15, 1);

            if (items.containsKey(29) && persona.getDirectivoEtec() == 0) {
                item = items.get(29);
                BigDecimal totalETEC = item.getImporte();
                for (CursoCargoDto cursoCargo : cursoCargoClient.findAllByLegajoAndNivel(legajoId, anho, mes, const_Nivel_Secundario)) {
                    horasETEC = horasETEC.add(cursoCargo.getHorasSemanales());
                }
                basicoETEC = basicoETEC.add(horasETEC.multiply(control.getHoraReferenciaEtec())).setScale(2, RoundingMode.HALF_UP);
                antiguedadETEC = basicoETEC.multiply(indices.getFirst()).setScale(2, RoundingMode.HALF_UP);
                presentismoETEC = basicoETEC.multiply(porcentajeAntiguedadETEC).setScale(2, RoundingMode.HALF_UP);
                adicionalETEC = totalETEC.subtract(basicoETEC).subtract(antiguedadETEC).subtract(presentismoETEC).setScale(2, RoundingMode.HALF_UP);

            }
            // Pongo debajo el control del básico porque más abajo lo toma para la impresión
            item = null;
            if (!items.containsKey(1)) {
                item = new ItemDto();
            } else {
                item = items.get(1);
            }
            paragraph = new Paragraph(new Phrase("Legajo: ", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase(Objects.requireNonNull(persona.getLegajoId()).toString(), new Font(Font.HELVETICA, 9, Font.BOLD)));
            paragraph.add(new Phrase("         Dependencia: ", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase(dependencia.getAcronimo(), new Font(Font.HELVETICA, 9, Font.BOLD)));
            paragraph.add(new Phrase("         Sueldo Básico: ", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase(new DecimalFormat("#,##0.00").format(item.getImporte().add(basicoETEC).setScale(2, RoundingMode.HALF_UP)),
                    new Font(Font.HELVETICA, 9, Font.BOLD)));
            paragraph.add(new Phrase("         Ingreso: ", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            .format(Objects.requireNonNull(persona.getAltaAdministrativa()).withOffsetSameInstant(ZoneOffset.UTC)),
                    new Font(Font.HELVETICA, 9, Font.BOLD)));
            paragraph.add(new Phrase("         Antigüedad: ", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase(MessageFormat.format("{0}.{1}", mesesAntiguedad / 12, mesesAntiguedad % 12),
                    new Font(Font.HELVETICA, 9, Font.BOLD)));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setMultipliedLeading(2f);
            document.add(paragraph);
            paragraph = new Paragraph(new Phrase("CBU: ", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase(legajoBanco.getCbu(), new Font(Font.HELVETICA, 9, Font.BOLD)));

            //////////////
            paragraph.add(new Phrase("              Aporte Jubilatorio: ", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase(control.getAporteJubilatorio(), new Font(Font.HELVETICA, 8)));
            //////////////

            paragraph.add(new Phrase("              Fecha Pago: ", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            .format(Objects.requireNonNull(control.getFechaPago()).withOffsetSameInstant(ZoneOffset.UTC)),
                    new Font(Font.HELVETICA, 9, Font.BOLD)));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setMultipliedLeading(2f);
            document.add(paragraph);

            //////////////
            paragraph = new Paragraph(new Phrase("              Período Liquidado: ", new Font(Font.HELVETICA, 10)));
            paragraph.add(new Phrase(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            .format(Objects.requireNonNull(control.getFechaDesde()).withOffsetSameInstant(ZoneOffset.UTC)),
                    new Font(Font.HELVETICA, 11, Font.BOLD)));
            paragraph.add(new Phrase("-", new Font(Font.HELVETICA, 11, Font.BOLD)));
            paragraph.add(new Phrase(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            .format(Objects.requireNonNull(control.getFechaHasta()).withOffsetSameInstant(ZoneOffset.UTC)),
                    new Font(Font.HELVETICA, 11, Font.BOLD)));
            //////////////

            paragraph.add(new Phrase("              Fecha Depósito: ", new Font(Font.HELVETICA, 8)));
            paragraph.add(new Phrase(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            .format(Objects.requireNonNull(control.getFechaDeposito()).withOffsetSameInstant(ZoneOffset.UTC)),
                    new Font(Font.HELVETICA, 9, Font.BOLD)));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setMultipliedLeading(2f);
            document.add(paragraph);
            // Cursos
            List<CursoCargoDto> cursos = cursoCargoClient.findAllByLegajo(legajoId, anho, mes);
            if (!cursos.isEmpty()) {
                float[] columnCurso = {4, 24, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f};
                PdfPTable tableCurso = new PdfPTable(columnCurso);
                tableCurso.setWidthPercentage(90);
                tableCurso.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(new Paragraph("Cargo", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tableCurso.addCell(cell);
                cell = new PdfPCell(new Paragraph("Curso", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableCurso.addCell(cell);
                cell = new PdfPCell(new Paragraph("Ds", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCurso.addCell(cell);
                cell = new PdfPCell(new Paragraph("An", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCurso.addCell(cell);
                cell = new PdfPCell(new Paragraph("S1", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCurso.addCell(cell);
                cell = new PdfPCell(new Paragraph("S2", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCurso.addCell(cell);
                cell = new PdfPCell(new Paragraph("Hr", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCurso.addCell(cell);
                for (CursoCargoDto cursoCargo : cursos) {
                    cell = new PdfPCell(new Paragraph(cursoCargo.getCargoTipo().getNombre(),
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCurso.addCell(cell);
                    cell = new PdfPCell(new Paragraph(cursoCargo.getCurso().getNombre(), new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCurso.addCell(cell);
                    cell = new PdfPCell(new Paragraph(cursoCargo.getDesarraigo() == 1 ? "*" : "",
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableCurso.addCell(cell);
                    cell = new PdfPCell(new Paragraph(cursoCargo.getCurso().getAnual() == 1 ? "*" : "",
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableCurso.addCell(cell);
                    cell = new PdfPCell(new Paragraph(cursoCargo.getCurso().getSemestre1() == 1 ? "*" : "",
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableCurso.addCell(cell);
                    cell = new PdfPCell(new Paragraph(cursoCargo.getCurso().getSemestre2() == 1 ? "*" : "",
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableCurso.addCell(cell);
                    cell = new PdfPCell(new Paragraph(new DecimalFormat("#0").format(cursoCargo.getHorasSemanales()),
                            new Font(Font.HELVETICA, 8, Font.BOLD)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tableCurso.addCell(cell);
                }
                document.add(tableCurso);
            }
            // Actividad Docente
            List<CargoLiquidacionDto> cargos = cargoLiquidacionClient.findAllDocenteByLegajo(legajoId, anho, mes);
            if (!cargos.isEmpty()) {
                paragraph = new Paragraph("Actividad Docente", new Font(Font.HELVETICA, 8, Font.BOLD));
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.setMultipliedLeading(2f);
                document.add(paragraph);
                // Cargos Liquidados
                float[] columnGrupo = {1, 1};
                PdfPTable tableGrupo = new PdfPTable(columnGrupo);
                tableGrupo.setWidthPercentage(90);
                tableGrupo.setHorizontalAlignment(Element.ALIGN_CENTER);
                float[] columnCargo = {2, 6, 3};
                PdfPTable tableCargo = new PdfPTable(columnCargo);
                tableCargo.setWidthPercentage(100);
                tableCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(new Paragraph("Dep", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(new Paragraph("Cargo", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(new Paragraph("Básico", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(tableCargo);
                cell.setBorder(Rectangle.NO_BORDER);
                tableGrupo.addCell(cell);
                tableGrupo.addCell(cell);
                document.add(tableGrupo);
                tableGrupo = new PdfPTable(columnGrupo);
                tableGrupo.setWidthPercentage(90);
                tableGrupo.setHorizontalAlignment(Element.ALIGN_CENTER);
                int count = 0;
                for (CargoLiquidacionDto cargoLiquidacion : cargos) {
                    count++;
                    tableCargo = new PdfPTable(columnCargo);
                    tableCargo.setWidthPercentage(100);
                    tableCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(new Paragraph(Objects.requireNonNull(cargoLiquidacion.getDependencia()).getAcronimo(),
                            new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(
                            new Paragraph(Objects.requireNonNull(cargoLiquidacion.getCategoria()).getNombre(), new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(
                            new Paragraph(new DecimalFormat("#,###.00").format(cargoLiquidacion.getCategoriaBasico()),
                                    new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(tableCargo);
                    cell.setBorder(Rectangle.NO_BORDER);
                    tableGrupo.addCell(cell);
                }
                // Total Adicionales
                Map<Integer, BigDecimal> adicionales = new HashMap<Integer, BigDecimal>();
                for (LiquidacionAdicionalDto liquidacionAdicional : liquidacionAdicionalClient.findAllByLegajo(legajoId,
                        anho, mes)) {
                    dependencia = dependenciaClient.findByDependenciaId(liquidacionAdicional.getDependenciaId());
                    if (!adicionales.containsKey(dependencia.getDependenciaId())) {
                        adicionales.put(dependencia.getDependenciaId(), BigDecimal.ZERO);
                    }
                    adicionales.put(dependencia.getDependenciaId(), adicionales.get(dependencia.getDependenciaId())
                            .add(liquidacionAdicional.getAdicional()).setScale(2, RoundingMode.HALF_UP));
                }
                for (Integer dependenciaId : adicionales.keySet()) {
                    count++;
                    dependencia = dependenciaClient.findByDependenciaId(dependenciaId);
                    tableCargo = new PdfPTable(columnCargo);
                    tableCargo.setWidthPercentage(100);
                    tableCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(new Paragraph(dependencia.getAcronimo(), new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCargo.addCell(cell);

                    CodigoDto codigo = codigoClient.findByCodigoId(981);
                    cell = new PdfPCell(new Paragraph(codigo.getNombre(), new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(
                            new Paragraph(new DecimalFormat("#,###.00").format(adicionales.get(dependenciaId)),
                                    new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(tableCargo);
                    cell.setBorder(Rectangle.NO_BORDER);
                    tableGrupo.addCell(cell);
                }
                // Completa con una celda vacía si la cantidad de cargos es impar
                if (count % 2 != 0) {
                    cell = new PdfPCell();
                    cell.setBorder(Rectangle.NO_BORDER);
                    tableGrupo.addCell(cell);
                }
                document.add(tableGrupo);
            }
            // Actividad No Docente
            cargos = cargoLiquidacionClient.findAllNoDocenteByLegajo(legajoId, anho, mes);
            if (!cargos.isEmpty()) {
                paragraph = new Paragraph("Actividad No Docente", new Font(Font.HELVETICA, 8, Font.BOLD));
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.setMultipliedLeading(2f);
                document.add(paragraph);
                // Cargos Liquidados
                float[] columnGrupo = {1, 1};
                PdfPTable tableGrupo = new PdfPTable(columnGrupo);
                tableGrupo.setWidthPercentage(90);
                tableGrupo.setHorizontalAlignment(Element.ALIGN_CENTER);
                float[] columnCargo = {2, 6, 3};
                PdfPTable tableCargo = new PdfPTable(columnCargo);
                tableCargo.setWidthPercentage(100);
                tableCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(new Paragraph("Dep", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(new Paragraph("Cargo", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(new Paragraph("Básico", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(tableCargo);
                cell.setBorder(Rectangle.NO_BORDER);
                tableGrupo.addCell(cell);
                tableGrupo.addCell(cell);
                document.add(tableGrupo);
                tableGrupo = new PdfPTable(columnGrupo);
                tableGrupo.setWidthPercentage(90);
                tableGrupo.setHorizontalAlignment(Element.ALIGN_CENTER);
                int count = 0;
                for (CargoLiquidacionDto cargoLiquidacion : cargos) {
                    count++;
                    tableCargo = new PdfPTable(columnCargo);
                    tableCargo.setWidthPercentage(100);
                    tableCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(new Paragraph(Objects.requireNonNull(cargoLiquidacion.getDependencia()).getAcronimo(),
                            new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(
                            new Paragraph(Objects.requireNonNull(cargoLiquidacion.getCategoria()).getNombre(), new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCargo.addCell(cell);
                    BigDecimal multiplicador = BigDecimal.ONE;
                    if (cargoLiquidacion.getHorasJornada().compareTo(BigDecimal.ZERO) == 0) {
                        multiplicador = new BigDecimal(cargoLiquidacion.getJornada());
                    } else {
                        multiplicador = cargoLiquidacion.getHorasJornada();
                    }
                    cell = new PdfPCell(new Paragraph(
                            new DecimalFormat("#,###.00").format(cargoLiquidacion.getCategoriaBasico()
                                    .multiply(multiplicador).setScale(2, RoundingMode.HALF_UP)),
                            new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(tableCargo);
                    cell.setBorder(Rectangle.NO_BORDER);
                    tableGrupo.addCell(cell);
                }
                // Completa con una celda vacía si la cantidad de cargos es impar
                if (count % 2 != 0) {
                    cell = new PdfPCell();
                    cell.setBorder(Rectangle.NO_BORDER);
                    tableGrupo.addCell(cell);
                }
                document.add(tableGrupo);
            }
            // Cargos con Clase
            List<CargoClaseDetalleDto> clases = cargoClaseDetalleClient.findAllByLegajo(legajoId, anho, mes);
            if (!clases.isEmpty()) {
                paragraph = new Paragraph("Actividad Académica", new Font(Font.HELVETICA, 8, Font.BOLD));
                paragraph.setAlignment(Element.ALIGN_CENTER);
                paragraph.setMultipliedLeading(2f);
                document.add(paragraph);
                float[] columnCargo = {2, 14, 3};
                PdfPTable tableCargo = new PdfPTable(columnCargo);
                tableCargo.setWidthPercentage(90);
                tableCargo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(new Paragraph("Dep", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(new Paragraph("Cargo", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableCargo.addCell(cell);
                cell = new PdfPCell(new Paragraph("Básico", new Font(Font.HELVETICA, 8, Font.BOLD)));
                cell.setBorder(Rectangle.BOTTOM);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tableCargo.addCell(cell);
                for (CargoClaseDetalleDto cargoClaseDetalle : clases) {
                    cell = new PdfPCell(new Paragraph(Objects.requireNonNull(cargoClaseDetalle.getDependencia()).getAcronimo(),
                            new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(new Paragraph(
                            MessageFormat.format("{0} / {1} / {2} / {3} / {4} {5}",
                                    Objects.requireNonNull(Objects.requireNonNull(cargoClaseDetalle.getCargoClase().getClase())).getNombre(),
                                    cargoClaseDetalle.getCargoClase().getNombre(),
                                    Objects.requireNonNull(cargoClaseDetalle.getCargoClasePeriodo()).getDescripcion(),
                                    Objects.requireNonNull(cargoClaseDetalle.getCargoClasePeriodo().getGeografica()).getNombre(),
                                    cargoClaseDetalle.getCargoClasePeriodo().getHoras(), "horas"),
                            new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCargo.addCell(cell);
                    cell = new PdfPCell(
                            new Paragraph(new DecimalFormat("#,###.00").format(
                                    cargoClaseDetalle.getValorHora()
                                            .multiply(new BigDecimal(cargoClaseDetalle.getHoras()))),
                                    new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCargo.addCell(cell);
                }
                document.add(tableCargo);
            }
            // Códigos Liquidados
            int count = 0;
            float[] columnGrupoCodigo = {1, 1, 1};
            PdfPTable tableGrupoCodigo = new PdfPTable(columnGrupoCodigo);
            tableGrupoCodigo.setWidthPercentage(100);
            tableGrupoCodigo.setHorizontalAlignment(Element.ALIGN_CENTER);
            float[] columnCodigo = {2, 10, 5};
            PdfPTable tableCodigoRemun = new PdfPTable(columnCodigo);
            tableCodigoRemun.setWidthPercentage(100);
            tableCodigoRemun.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(new Paragraph("Cód", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCodigoRemun.addCell(cell);
            cell = new PdfPCell(new Paragraph("Concepto", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableCodigoRemun.addCell(cell);
            cell = new PdfPCell(new Paragraph("Remun", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCodigoRemun.addCell(cell);
            PdfPTable tableCodigoNoRemun = new PdfPTable(columnCodigo);
            tableCodigoNoRemun.setWidthPercentage(100);
            tableCodigoNoRemun.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(new Paragraph("Cód", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCodigoNoRemun.addCell(cell);
            cell = new PdfPCell(new Paragraph("Concepto", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableCodigoNoRemun.addCell(cell);
            cell = new PdfPCell(new Paragraph("No Rem", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCodigoNoRemun.addCell(cell);
            PdfPTable tableCodigoDeduc = new PdfPTable(columnCodigo);
            tableCodigoDeduc.setWidthPercentage(100);
            tableCodigoDeduc.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(new Paragraph("Cód", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCodigoDeduc.addCell(cell);
            cell = new PdfPCell(new Paragraph("Concepto", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableCodigoDeduc.addCell(cell);
            cell = new PdfPCell(new Paragraph("Deduc", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCodigoDeduc.addCell(cell);
            // Detalle Remunerativo
            count = 0;
            BigDecimal totalRemunerativo = BigDecimal.ZERO;
            for (CodigoGrupoDto codigoGrupo : codigoGrupoClient.findAllByRemunerativo((byte) 1)) {
                if (items.containsKey(codigoGrupo.getCodigoId())) {
                    item = items.get(codigoGrupo.getCodigoId());
                    if (item.getCodigoId() == 29 && persona.getDirectivoEtec() == 0) {
                        // basico ETEC
                        cell = new PdfPCell(
                                new Paragraph(Objects.requireNonNull(codigoGrupo.getCodigoId()).toString(), new Font(Font.HELVETICA, 8)));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tableCodigoRemun.addCell(cell);
                        cell = new PdfPCell(
                                new Paragraph("BASICO ETEC", new Font(Font.HELVETICA, 8)));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        tableCodigoRemun.addCell(cell);
                        cell = new PdfPCell(new Paragraph(new DecimalFormat("#,##0.00").format(basicoETEC),
                                new Font(Font.HELVETICA, 8)));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tableCodigoRemun.addCell(cell);
                        totalRemunerativo = totalRemunerativo.add(basicoETEC).setScale(2, RoundingMode.HALF_UP);
                        count++;
                        // antiguedad ETEC
                        cell = new PdfPCell(
                                new Paragraph(codigoGrupo.getCodigoId().toString(), new Font(Font.HELVETICA, 8)));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tableCodigoRemun.addCell(cell);
                        cell = new PdfPCell(
                                new Paragraph("ANTIGUEDAD ETEC", new Font(Font.HELVETICA, 8)));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        tableCodigoRemun.addCell(cell);
                        cell = new PdfPCell(new Paragraph(new DecimalFormat("#,##0.00").format(antiguedadETEC),
                                new Font(Font.HELVETICA, 8)));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tableCodigoRemun.addCell(cell);
                        totalRemunerativo = totalRemunerativo.add(antiguedadETEC).setScale(2, RoundingMode.HALF_UP);
                        count++;
                        // presentismo ETEC
                        cell = new PdfPCell(
                                new Paragraph(codigoGrupo.getCodigoId().toString(), new Font(Font.HELVETICA, 8)));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tableCodigoRemun.addCell(cell);
                        cell = new PdfPCell(
                                new Paragraph("PRESENTISMO ETEC", new Font(Font.HELVETICA, 8)));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        tableCodigoRemun.addCell(cell);
                        cell = new PdfPCell(new Paragraph(new DecimalFormat("#,##0.00").format(presentismoETEC),
                                new Font(Font.HELVETICA, 8)));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tableCodigoRemun.addCell(cell);
                        totalRemunerativo = totalRemunerativo.add(presentismoETEC).setScale(2, RoundingMode.HALF_UP);
                        count++;
                        // adicional ETEC
                        cell = new PdfPCell(
                                new Paragraph(codigoGrupo.getCodigoId().toString(), new Font(Font.HELVETICA, 8)));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tableCodigoRemun.addCell(cell);
                        cell = new PdfPCell(
                                new Paragraph("ADICIONAL ETEC", new Font(Font.HELVETICA, 8)));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        tableCodigoRemun.addCell(cell);
                        cell = new PdfPCell(new Paragraph(new DecimalFormat("#,##0.00").format(adicionalETEC),
                                new Font(Font.HELVETICA, 8)));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tableCodigoRemun.addCell(cell);
                        totalRemunerativo = totalRemunerativo.add(adicionalETEC).setScale(2, RoundingMode.HALF_UP);
                        count++;
                    } else {
                        cell = new PdfPCell(
                                new Paragraph(Objects.requireNonNull(codigoGrupo.getCodigoId()).toString(), new Font(Font.HELVETICA, 8)));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tableCodigoRemun.addCell(cell);
                        cell = new PdfPCell(
                                new Paragraph(Objects.requireNonNull(codigoGrupo.getCodigo()).getNombre(), new Font(Font.HELVETICA, 8)));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        tableCodigoRemun.addCell(cell);
                        cell = new PdfPCell(new Paragraph(new DecimalFormat("#,##0.00").format(item.getImporte()),
                                new Font(Font.HELVETICA, 8)));
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tableCodigoRemun.addCell(cell);
                        totalRemunerativo = totalRemunerativo.add(item.getImporte()).setScale(2, RoundingMode.HALF_UP);
                        count++;
                    }
                }
            }
            // Si no hay códigos remunerativos
            if (count == 0) {
                cell = new PdfPCell(new Paragraph(" ", new Font(Font.HELVETICA, 8)));
                cell.setBorder(Rectangle.NO_BORDER);
                tableCodigoRemun.addCell(cell);
                cell = new PdfPCell(new Paragraph(" ", new Font(Font.HELVETICA, 8)));
                cell.setBorder(Rectangle.NO_BORDER);
                tableCodigoRemun.addCell(cell);
                cell = new PdfPCell(new Paragraph(" ", new Font(Font.HELVETICA, 8)));
                cell.setBorder(Rectangle.NO_BORDER);
                tableCodigoRemun.addCell(cell);
            }
            // Detalle No Remunerativo
            count = 0;
            BigDecimal totalNoRemunerativo = BigDecimal.ZERO;
            for (CodigoGrupoDto codigoGrupo : codigoGrupoClient.findAllByNoRemunerativo((byte) 1)) {
                if (items.containsKey(codigoGrupo.getCodigoId())) {
                    item = items.get(codigoGrupo.getCodigoId());
                    cell = new PdfPCell(
                            new Paragraph(codigoGrupo.getCodigoId().toString(), new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCodigoNoRemun.addCell(cell);
                    cell = new PdfPCell(
                            new Paragraph(codigoGrupo.getCodigo().getNombre(), new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCodigoNoRemun.addCell(cell);
                    cell = new PdfPCell(new Paragraph(new DecimalFormat("#,##0.00").format(item.getImporte()),
                            new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCodigoNoRemun.addCell(cell);
                    totalNoRemunerativo = totalNoRemunerativo.add(item.getImporte()).setScale(2, RoundingMode.HALF_UP);
                    count++;
                }
            }
            // Si no hay códigos no remunerativos
            if (count == 0) {
                cell = new PdfPCell(new Paragraph(" ", new Font(Font.HELVETICA, 8)));
                cell.setBorder(Rectangle.NO_BORDER);
                tableCodigoNoRemun.addCell(cell);
                cell = new PdfPCell(new Paragraph(" ", new Font(Font.HELVETICA, 8)));
                cell.setBorder(Rectangle.NO_BORDER);
                tableCodigoNoRemun.addCell(cell);
                cell = new PdfPCell(new Paragraph(" ", new Font(Font.HELVETICA, 8)));
                cell.setBorder(Rectangle.NO_BORDER);
                tableCodigoNoRemun.addCell(cell);
            }
            // Detalle Deducciones
            count = 0;
            BigDecimal totalDeduccion = BigDecimal.ZERO;
            for (CodigoGrupoDto codigoGrupo : codigoGrupoClient.findAllByDeduccion((byte) 1)) {
                if (items.containsKey(codigoGrupo.getCodigoId())) {
                    item = items.get(codigoGrupo.getCodigoId());
                    cell = new PdfPCell(
                            new Paragraph(Objects.requireNonNull(codigoGrupo.getCodigoId()).toString(), new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCodigoDeduc.addCell(cell);
                    cell = new PdfPCell(
                            new Paragraph(Objects.requireNonNull(codigoGrupo.getCodigo()).getNombre(), new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tableCodigoDeduc.addCell(cell);
                    cell = new PdfPCell(new Paragraph(new DecimalFormat("#,##0.00").format(item.getImporte()),
                            new Font(Font.HELVETICA, 8)));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tableCodigoDeduc.addCell(cell);
                    totalDeduccion = totalDeduccion.add(item.getImporte()).setScale(2, RoundingMode.HALF_UP);
                    count++;
                }
            }
            // Si no hay códigos deducciones
            if (count == 0) {
                cell = new PdfPCell(new Paragraph(" ", new Font(Font.HELVETICA, 8)));
                cell.setBorder(Rectangle.NO_BORDER);
                tableCodigoDeduc.addCell(cell);
                cell = new PdfPCell(new Paragraph(" ", new Font(Font.HELVETICA, 8)));
                cell.setBorder(Rectangle.NO_BORDER);
                tableCodigoDeduc.addCell(cell);
                cell = new PdfPCell(new Paragraph(" ", new Font(Font.HELVETICA, 8)));
                cell.setBorder(Rectangle.NO_BORDER);
                tableCodigoDeduc.addCell(cell);
            }

            cell = new PdfPCell(tableCodigoRemun);
            cell.setBorder(Rectangle.NO_BORDER);
            tableGrupoCodigo.addCell(cell);
            cell = new PdfPCell(tableCodigoNoRemun);
            cell.setBorder(Rectangle.NO_BORDER);
            tableGrupoCodigo.addCell(cell);
            cell = new PdfPCell(tableCodigoDeduc);
            cell.setBorder(Rectangle.NO_BORDER);
            tableGrupoCodigo.addCell(cell);
            document.add(tableGrupoCodigo);
            // Totales
            tableGrupoCodigo = new PdfPTable(columnGrupoCodigo);
            tableGrupoCodigo.setWidthPercentage(100);
            tableGrupoCodigo.setHorizontalAlignment(Element.ALIGN_CENTER);
            // Agregando Remunerativos
            tableCodigoRemun = new PdfPTable(columnCodigo);
            tableCodigoRemun.setWidthPercentage(100);
            tableCodigoRemun.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(new Paragraph("", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCodigoRemun.addCell(cell);
            cell = new PdfPCell(new Paragraph("Remunerativos", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCodigoRemun.addCell(cell);
            cell = new PdfPCell(new Paragraph(new DecimalFormat("#,##0.00").format(totalRemunerativo),
                    new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCodigoRemun.addCell(cell);
            cell = new PdfPCell(tableCodigoRemun);
            cell.setBorder(Rectangle.NO_BORDER);
            tableGrupoCodigo.addCell(cell);
            // Agregando No Remunerativos
            tableCodigoNoRemun = new PdfPTable(columnCodigo);
            tableCodigoNoRemun.setWidthPercentage(100);
            tableCodigoNoRemun.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(new Paragraph("", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCodigoNoRemun.addCell(cell);
            cell = new PdfPCell(new Paragraph("No Remunerativos", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCodigoNoRemun.addCell(cell);
            cell = new PdfPCell(new Paragraph(new DecimalFormat("#,##0.00").format(totalNoRemunerativo),
                    new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCodigoNoRemun.addCell(cell);
            cell = new PdfPCell(tableCodigoNoRemun);
            cell.setBorder(Rectangle.NO_BORDER);
            tableGrupoCodigo.addCell(cell);
            // Agregando Deducciones
            tableCodigoDeduc = new PdfPTable(columnCodigo);
            tableCodigoDeduc.setWidthPercentage(100);
            tableCodigoDeduc.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(new Paragraph("", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCodigoDeduc.addCell(cell);
            cell = new PdfPCell(new Paragraph("Deducciones", new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCodigoDeduc.addCell(cell);
            cell = new PdfPCell(new Paragraph(new DecimalFormat("#,##0.00").format(totalDeduccion),
                    new Font(Font.HELVETICA, 8, Font.BOLD)));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableCodigoDeduc.addCell(cell);
            cell = new PdfPCell(tableCodigoDeduc);
            cell.setBorder(Rectangle.NO_BORDER);
            tableGrupoCodigo.addCell(cell);
            document.add(tableGrupoCodigo);
            // Firma y Letras
            LetraDto letra = null;
            BigDecimal neto = BigDecimal.ZERO;
            if (items.containsKey(99)) {
                neto = items.get(99).getImporte();
            }
            try {
                letra = letraClient.findByUnique(legajoId, anho, mes);
                letra = letraClient.update(
                        new LetraDto(letra.getLetraId(), legajoId, anho, mes, neto, Tool.number_2_text(neto)),
                        letra.getLetraId());
            } catch (Exception e) {
                letra = letraClient.add(new LetraDto(null, legajoId, anho, mes, neto, Tool.number_2_text(neto)));
            }
            PdfPTable tableFirma = new PdfPTable(columnHeader);
            tableFirma.setWidthPercentage(100);
            cell = new PdfPCell(firma);
            cell.setBorder(Rectangle.NO_BORDER);
            tableFirma.addCell(cell);
            paragraph = new Paragraph(new Phrase("Neto: ", new Font(Font.HELVETICA, 10)));
            paragraph.add(new Phrase(new DecimalFormat("#,##0.00").format(letra.getNeto()),
                    new Font(Font.HELVETICA, 10, Font.BOLD)));
            paragraph.add(new Phrase("\nson pesos: ", new Font(Font.HELVETICA, 10)));
            paragraph.add(new Phrase(letra.getCadena(), new Font(Font.HELVETICA, 10, Font.BOLD)));
            cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setLeading(0, 2f);
            tableFirma.addCell(cell);
            document.add(tableFirma);
            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        bonoImpresionClient.add(new BonoImpresionDto(null, legajoId, anho, mes, legajoIdSolicitud,
                Tool.hourAbsoluteArgentina(), ipAddress));

        return filename;
    }

    public String sendBono(Long legajoId, Integer anho, Integer mes, Long legajoIdSolicitud, String ipAddress)
            throws MessagingException {
        // Genera PDF
        String filenameBono = this.generatePdf(legajoId, anho, mes, legajoIdSolicitud, ipAddress);
        log.info("Filename_bono -> " + filenameBono);
        if (filenameBono.isEmpty()) {
            return "ERROR: Sin Chequera para ENVIAR";
        }

        String data = "";

        PersonaDto persona = personaClient.findByLegajoId(legajoId);

        ContactoDto contacto = null;
        try {
            contacto = contactoClient.findByLegajoId(legajoId);
        } catch (Exception e) {
            return "ERROR: Sin correos para ENVIAR";
        }

        data = "Estimad@ " + persona.getApellidoNombre() + ": " + (char) 10;
        data = data + (char) 10;
        data = data + "Le enviamos como archivo adjunto su bono de sueldo." + (char) 10;
        data = data + (char) 10;
        data = data + "Atentamente." + (char) 10;
        data = data + (char) 10;
        data = data + "Universidad de Mendoza" + (char) 10;
        data = data + (char) 10;
        data = data + (char) 10
                + "Por favor no responda este mail, fue generado automáticamente. Su respuesta no será leída."
                + (char) 10;

        // Envia correo
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        List<String> addresses = new ArrayList<String>();

        if (!contacto.getMailInstitucional().isEmpty())
            addresses.add(contacto.getMailInstitucional());

        try {
            helper.setTo(addresses.toArray(new String[addresses.size()]));
            helper.setText(data);
            helper.setReplyTo("no-reply@um.edu.ar");
            helper.setSubject("Envío Automático de Bono de Sueldo -> " + filenameBono);

            FileSystemResource fileBono = new FileSystemResource(filenameBono);
            helper.addAttachment(filenameBono, fileBono);

        } catch (MessagingException e) {
            e.printStackTrace();
            return "ERROR: No pudo ENVIARSE";
        }

        javaMailSender.send(message);

        LegajoControlDto legajoControl = null;
        try {
            legajoControl = legajoControlClient.findByUnique(legajoId, anho, mes);
            legajoControl.setBonoEnviado((byte) 1);
            legajoControl = legajoControlClient.update(legajoControl, legajoControl.getLegajoControlId());
        } catch (Exception e) {
            legajoControl = new LegajoControlDto(null, legajoId, anho, mes, (byte) 0, (byte) 0, (byte) 1, persona);
            legajoControl = legajoControlClient.add(legajoControl);
        }

        return "Envío de Correo Ok!!";
    }

}
