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
import um.haberes.report.client.haberes.core.DependenciaClient;
import um.haberes.report.client.haberes.core.LiquidacionClient;
import um.haberes.report.client.haberes.core.PersonaClient;
import um.haberes.report.kotlin.dto.haberes.core.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComparacionRemuneracionesService {
    private final Environment environment;
    private final LiquidacionClient liquidacionClient;
    private final PersonaClient personaClient;
    private final DependenciaClient dependenciaClient;

    public String generate(Integer anho, Integer mes, Integer anho_anterior, Integer mes_anterior) {
        String path = environment.getProperty("path.files");
        String filename = path + "comparacion-remuneraciones." + anho + "." + mes + "." + anho_anterior + "." + mes_anterior + ".pdf";

        generateReport(filename, anho, mes, anho_anterior, mes_anterior);
        return filename;
    }

    private void generateReport(String filename, Integer anho, Integer mes, Integer anho_anterior, Integer mes_anterior) {
        log.debug("Generating report for anho {}, mes {}, anho_anterior {}, mes_anterior {}", anho, mes, anho_anterior, mes_anterior);
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            log.debug("writer created");
            ComparacionRemuneracionesService.HeaderFooter event = new ComparacionRemuneracionesService.HeaderFooter(anho, mes, anho_anterior, mes_anterior);
            log.debug("event created");
            writer.setPageEvent(event);
            log.debug("page event set");
            document.open();
            log.debug("document opened");

            // Encabezado del documento
            event.setHeader(document);
            log.debug("header set");

            List<LiquidacionDto> liquidacionesActual = liquidacionClient.findAllByAcreditado(anho, mes);
            if (liquidacionesActual == null || liquidacionesActual.isEmpty()) {
                log.warn("No se encontraron totales de novedades para el periodo {}-{}", anho, mes);
                return;
            }

            List<LiquidacionDto> liquidacionesAnterior = liquidacionClient.findAllByAcreditado(anho_anterior, mes_anterior);
            if (liquidacionesAnterior == null || liquidacionesAnterior.isEmpty()) {
                log.warn("No se encontraron totales de novedades para el periodo {}-{}", anho_anterior, mes_anterior);
                return;
            }

            List<PersonaDto> personas = personaClient.findAll();
            if (personas == null || personas.isEmpty()) {
                log.warn("No se encontraron personas");
                return;
            }

            List<DependenciaDto> dependencias = dependenciaClient.findAll();
            if (dependencias == null || dependencias.isEmpty()) {
                log.warn("No se encontraron dependencias");
                return;
            }

            // Verificar si las listas de liquidaciones tienen datos
            log.debug("Total liquidaciones actuales: {}", liquidacionesActual.size());
            log.debug("Total liquidaciones anteriores: {}", liquidacionesAnterior.size());
            log.debug("Total personas: {}", personas.size());

          // Convertir listas en mapas por legajo

            // Mapear liquidaciones por legajo
            Map<Long, LiquidacionDto> liquidacionesActualMap = liquidacionesActual.stream()
                    .collect(Collectors.toMap(LiquidacionDto::getLegajoId, l -> l, (l1, l2) -> l1));

            Map<Long, LiquidacionDto> liquidacionesAnteriorMap = liquidacionesAnterior.stream()
                    .collect(Collectors.toMap(LiquidacionDto::getLegajoId, l -> l, (l1, l2) -> l1));

            Map<Integer, DependenciaDto> dependenciaMap = dependencias.stream()
                    .collect(Collectors.toMap(DependenciaDto::getDependenciaId, d -> d));

            Map<Integer, List<PersonaDto>> personasPorDependencia = personas.stream()
                    .filter(p -> p.getDependenciaId() != null)
                    .collect(Collectors.groupingBy(PersonaDto::getDependenciaId));

            // 🔹 Ordenar las dependencias alfabéticamente por nombre
            List<Integer> dependenciasOrdenadas = dependenciaMap.values().stream()
                    .map(DependenciaDto::getDependenciaId)
                    .sorted()
                    .toList();

            for (Integer dependenciaId : dependenciasOrdenadas) {
                List<PersonaDto> personasDeDependencia = personasPorDependencia.get(dependenciaId);

                if (personasDeDependencia == null || personasDeDependencia.isEmpty()) {
                    continue; // Saltar dependencias sin personas
                }

                DependenciaDto dependencia = dependenciaMap.get(dependenciaId);

                // 🔹 Título de la dependencia
                Paragraph dependenciaTitle = new Paragraph(
                        (dependencia != null ? dependencia.getNombre() : "Sin nombre"),
                        new Font(Font.HELVETICA, 12, Font.BOLD)
                );
                dependenciaTitle.setSpacingBefore(10);
                dependenciaTitle.setSpacingAfter(5);
                document.add(dependenciaTitle);

                PdfPTable detailTable = new PdfPTable(9);
                float[] columnHeader = {8, 30, 20, 20, 20, 20, 20, 20, 20};
                detailTable.setWidthPercentage(95);
                detailTable.setWidths(columnHeader);
                addTableHeader(detailTable);

                // 🔹 Ordenar personas alfabéticamente por apellido y nombre
                personasDeDependencia.stream()
                        .sorted(Comparator
                                .comparing(PersonaDto::getApellido, Comparator.nullsLast(String::compareToIgnoreCase))
                                .thenComparing(PersonaDto::getNombre, Comparator.nullsLast(String::compareToIgnoreCase))
                        )
                        .forEach(persona -> {
                            Long legajoId = persona.getLegajoId();
                            LiquidacionDto actual = liquidacionesActualMap.get(legajoId);

                            // 🚨 Si no hay liquidación actual, no se incluye la persona
                            if (actual == null) {
                                log.warn("No se encontró liquidación actual para legajo ID: {}", legajoId);
                                return;
                            }

                            LiquidacionDto anterior = liquidacionesAnteriorMap.getOrDefault(legajoId, new LiquidacionDto());

                            addComparacionRemuneracionesDetails(document, actual, anterior, detailTable, persona);
                        });

                document.add(detailTable);
            }

            document.close();
        } catch (Exception e) {
            log.debug("Error generating report {}", e.getMessage());
        }
    }

    private void addTableHeader(PdfPTable table) {
        Font headerFont = new Font(Font.HELVETICA, 8, Font.BOLD);

        // Fila superior: Bruto y Neto
        PdfPCell emptyCell = new PdfPCell(new Phrase(""));
        emptyCell.setColspan(2); // "Leg" y "Apellido, Nombre"
        emptyCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(emptyCell);

        PdfPCell brutoCell = new PdfPCell(new Phrase("Bruto", headerFont));
        brutoCell.setColspan(4); // Anterior, Actual, Dif, Porcentaje
        brutoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        brutoCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(brutoCell);

        PdfPCell netoCell = new PdfPCell(new Phrase("Neto", headerFont));
        netoCell.setColspan(3); // Anterior, Actual, Dif
        netoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        netoCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(netoCell);

        PdfPCell cell = new PdfPCell(new Phrase("Leg", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Apellido, Nombre", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Anterior", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Actual", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Dif", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Anterior", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Actual", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Dif", headerFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }

    private void addComparacionRemuneracionesDetails(Document document, LiquidacionDto actual, LiquidacionDto anterior, PdfPTable detailTable, PersonaDto persona) throws DocumentException {

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "ES"));

        // Asegurar que los valores nunca sean null
        BigDecimal totalRemunerativoActual = actual.getTotalRemunerativo() != null ? actual.getTotalRemunerativo() : BigDecimal.ZERO;
        BigDecimal totalRemunerativoAnterior = anterior.getTotalRemunerativo() != null ? anterior.getTotalRemunerativo() : BigDecimal.ZERO;
        BigDecimal totalNetoActual = actual.getTotalNeto() != null ? actual.getTotalNeto() : BigDecimal.ZERO;
        BigDecimal totalNetoAnterior = anterior.getTotalNeto() != null ? anterior.getTotalNeto() : BigDecimal.ZERO;

// Calcular diferencia y porcentaje
        BigDecimal diferenciaBruto = totalRemunerativoActual.subtract(totalRemunerativoAnterior.abs());
        BigDecimal diferenciaNeto = totalNetoActual.subtract(totalNetoAnterior.abs());
        BigDecimal porcentajeBruto = totalRemunerativoAnterior.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : diferenciaBruto.multiply(BigDecimal.valueOf(100)).divide(totalRemunerativoAnterior, 2, RoundingMode.HALF_UP);

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
        paragraph.add(new Phrase(nf.format(totalRemunerativoAnterior.doubleValue()), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(nf.format(totalRemunerativoActual.doubleValue()), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(nf.format(diferenciaBruto.doubleValue()), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(nf.format(porcentajeBruto.doubleValue()) + "%", new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(nf.format(totalNetoAnterior.doubleValue()), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(nf.format(totalNetoActual.doubleValue()), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);

        paragraph = new Paragraph();
        paragraph.add(new Phrase(nf.format(diferenciaNeto.doubleValue()), new Font(Font.HELVETICA, 8)));
        cell = new PdfPCell(paragraph);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        detailTable.addCell(cell);
    }

    static class HeaderFooter extends PdfPageEventHelper {
        private final Integer anho;
        private final Integer mes;
        private final Integer anho_anterior;
        private final Integer mes_anterior;
        private PdfPTable headerTable;

        public HeaderFooter(Integer anho, Integer mes, Integer anho_anterior, Integer mes_anterior) {
            this.anho = anho;
            this.mes = mes;
            this.anho_anterior = anho_anterior;
            this.mes_anterior = mes_anterior;
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

            Paragraph titleParagraph = new Paragraph("Comparación de Remuneraciones", new Font(Font.HELVETICA, 13, Font.BOLD));
            titleParagraph.setAlignment(Element.ALIGN_CENTER);

            PdfPCell titleCell = new PdfPCell(titleParagraph);
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleTable.addCell(titleCell);

            // 🔹 Nueva línea con los periodos Actual y Anterior
            Paragraph periodsParagraph = new Paragraph(
                    "Actual: " + mes + "/" + anho + "    Anterior: " + mes_anterior + "/" + anho_anterior,
                    new Font(Font.HELVETICA, 11)
            );
            periodsParagraph.setAlignment(Element.ALIGN_CENTER);

            PdfPCell periodsCell = new PdfPCell(periodsParagraph);
            periodsCell.setBorder(Rectangle.NO_BORDER);
            periodsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            periodsCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titleTable.addCell(periodsCell);

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


