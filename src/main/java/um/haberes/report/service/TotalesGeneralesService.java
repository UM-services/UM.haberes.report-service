package um.haberes.report.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import um.haberes.report.client.haberes.core.CodigoClient;
import um.haberes.report.client.haberes.core.CodigoGrupoClient;
import um.haberes.report.client.haberes.core.TotalMensualClient;
import um.haberes.report.kotlin.dto.haberes.core.CodigoDto;
import um.haberes.report.kotlin.dto.haberes.core.CodigoGrupoDto;
import um.haberes.report.kotlin.dto.haberes.core.TotalMensualDto;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TotalesGeneralesService {

    private final Environment environment;
    private final CodigoClient codigoClient;
    private final CodigoGrupoClient codigoGrupoClient;
    private final TotalMensualClient totalMensualClient;

    public TotalesGeneralesService(Environment environment, CodigoClient codigoClient, CodigoGrupoClient codigoGrupoClient, TotalMensualClient totalMensualClient) {
        this.environment = environment;
        this.codigoClient = codigoClient;
        this.codigoGrupoClient = codigoGrupoClient;
        this.totalMensualClient = totalMensualClient;
    }

    public String generate(Integer anho, Integer mes) {
        String path = environment.getProperty("path.files");
        String filename = path + "totales.generales." + "." + anho + "." + mes + ".pdf";

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

            // Obtener totales y códigos
            Map<Integer, TotalMensualDto> totales = totalMensualClient.findAllByPeriodo(anho, mes)
                    .stream()
                    .collect(Collectors.toMap(TotalMensualDto::getCodigoId, total -> total));

            List<CodigoDto> codigos = codigoClient.findAllByPeriodo(anho, mes);
            List<CodigoGrupoDto> grupos = codigoGrupoClient.findAll();

            // Definir el orden fijo de los grupos
            List<String> ordenFijoGrupos = Arrays.asList("Remunerativo", "No Remunerativo", "Deducción", "Total");

            // Agrupar códigos por tipo de grupo y ordenar por codigoId dentro de cada grupo
            Map<String, List<CodigoDto>> codigosPorGrupo = new HashMap<>();
            for (CodigoGrupoDto grupo : grupos) {
                List<CodigoDto> codigosGrupo = codigos.stream()
                        .filter(codigo -> codigo.getCodigoId() != null && grupo.getCodigo() != null
                                && codigo.getCodigoId().equals(grupo.getCodigo().getCodigoId()))
                        .sorted(Comparator.comparing(CodigoDto::getCodigoId)) // Ordenar por codigoId
                        .collect(Collectors.toList());

                if (grupo.getRemunerativo() == 1) {
                    codigosPorGrupo.computeIfAbsent("Remunerativo", k -> new ArrayList<>()).addAll(codigosGrupo);
                }
                if (grupo.getNoRemunerativo() == 1) {
                    codigosPorGrupo.computeIfAbsent("No Remunerativo", k -> new ArrayList<>()).addAll(codigosGrupo);
                }
                if (grupo.getDeduccion() == 1) {
                    codigosPorGrupo.computeIfAbsent("Deducción", k -> new ArrayList<>()).addAll(codigosGrupo);
                }
                if (grupo.getTotal() == 1) {
                    codigosPorGrupo.computeIfAbsent("Total", k -> new ArrayList<>()).addAll(codigosGrupo);
                }
            }

            // Crear un formateador para números con coma y punto
            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("en", "ES"));

            // Generar reporte por cada grupo en el orden definido
            for (String tipoGrupo : ordenFijoGrupos) {
                if (!codigosPorGrupo.containsKey(tipoGrupo)) {
                    continue; // Saltar si no hay datos para este grupo
                }

                List<CodigoDto> codigosDelGrupo = codigosPorGrupo.get(tipoGrupo);

                // Crear una nueva tabla para cada grupo
                PdfPTable detailTable = new PdfPTable(3);
                float[] columnHeader = {15, 70, 15};
                detailTable.setWidthPercentage(95);
                detailTable.setWidths(columnHeader);

                // Agregar encabezado de la tabla
                addTableHeader(detailTable);

                // Calcular y agregar detalles del grupo
                BigDecimal totalGrupo = BigDecimal.ZERO;
                for (CodigoDto codigo : codigosDelGrupo) {
                    addCodigoDetails(document, codigo, detailTable, totales.get(codigo.getCodigoId()));

                    if (totales.get(codigo.getCodigoId()) != null) {
                        totalGrupo = totalGrupo.add(totales.get(codigo.getCodigoId()).getTotal());
                    }
                }

                // Formatear el subtotal
                String formattedTotal = numberFormat.format(totalGrupo);

                // Agregar subtotal del grupo
                PdfPCell emptyCell = new PdfPCell(new Phrase("")); // Celdas vacías para alinear el subtotal
                emptyCell.setBorder(Rectangle.NO_BORDER);

                PdfPCell subtotalLabelCell = new PdfPCell();
                subtotalLabelCell.setBorder(Rectangle.NO_BORDER);
                subtotalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

                PdfPCell subtotalValueCell = new PdfPCell(new Phrase(formattedTotal, new Font(Font.HELVETICA, 8, Font.BOLD)));
                subtotalValueCell.setBorder(Rectangle.NO_BORDER);
                subtotalValueCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                detailTable.addCell(emptyCell);
                detailTable.addCell(subtotalLabelCell);
                detailTable.addCell(subtotalValueCell);
                document.add(Chunk.NEWLINE);

                // Espacio entre grupos
                document.add(detailTable);
                document.add(Chunk.NEWLINE);

            }


            document.close();

        } catch (DocumentException | IOException e) {
            log.debug("Error generating report {}", e.getMessage());
        }
    }

    private void addTableHeader(PdfPTable table) {
        Font headerFont = new Font(Font.HELVETICA, 8, Font.BOLD);

        PdfPCell cell = new PdfPCell(new Phrase("Código", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Descripción", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Total", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

    }


    private void addCodigoDetails(Document document, CodigoDto codigo, PdfPTable detailTable, TotalMensualDto totalMensual) throws DocumentException {

        // Datos de la tabla
        var paragraph = new Paragraph();
        paragraph.add(new Phrase(Objects.requireNonNull(codigo.getCodigoId()).toString(), new Font(Font.HELVETICA, 8)));
        var cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(codigo.getNombre(), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(totalMensual.getTotal().toString(), new Font(Font.HELVETICA, 8)));
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

            // Nueva fila para "Totales Generales"
            PdfPTable titleTable = new PdfPTable(1); // Solo una columna para centrar
            titleTable.setWidthPercentage(100);

            Paragraph titleParagraph = new Paragraph("Totales Generales", new Font(Font.HELVETICA, 13, Font.BOLD));
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