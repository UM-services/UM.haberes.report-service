package um.haberes.report.reports.totalesgenerales.infrastructure.render;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalesGeneralesContent;
import um.haberes.report.reports.totalesgenerales.domain.ports.out.TotalesGeneralesXlsxRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Exporta el contenido del reporte a XLSX: un bloque por grupo con encabezado y subtotal,
 * replicando las columnas del PDF (Código, Descripción, Total).
 */
@Component
public class TotalesGeneralesXlsxRendererImpl implements TotalesGeneralesXlsxRenderer {

    private static final String SHEET_NAME = "Totales Generales";

    @Override
    public void render(TotalesGeneralesContent content, Path target) {
        try (Workbook book = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(target)) {
            CellStyle normal = style(book, false);
            CellStyle bold = style(book, true);
            Sheet sheet = book.createSheet(SHEET_NAME);

            int fila = 0;
            Row title = sheet.createRow(fila);
            string(title, 0, "Totales Generales", bold);
            Row period = sheet.createRow(++fila);
            string(period, 0, "Período: " + content.mes() + "/" + content.anho(), normal);
            ++fila;

            if (content.grupos().isEmpty()) {
                Row empty = sheet.createRow(fila);
                string(empty, 0, "Sin datos para el período.", bold);
            }

            for (TotalesGeneralesContent.Grupo grupo : content.grupos()) {
                Row group = sheet.createRow(fila++);
                string(group, 0, grupo.nombre(), bold);

                Row header = sheet.createRow(fila++);
                string(header, 0, "Código", bold);
                string(header, 1, "Descripción", bold);
                string(header, 2, "Total", bold);

                for (TotalesGeneralesContent.Fila row : grupo.filas()) {
                    Row data = sheet.createRow(fila++);
                    string(data, 0, row.codigoId(), normal);
                    string(data, 1, row.nombre(), normal);
                    string(data, 2, row.total(), normal);
                }

                Row subtotal = sheet.createRow(fila++);
                string(subtotal, 1, "Subtotal", bold);
                string(subtotal, 2, grupo.subtotal(), bold);
                fila++;
            }

            for (int column = 0; column <= 2; column++) {
                sheet.autoSizeColumn(column);
            }
            book.write(output);
        } catch (IOException e) {
            throw new ReportException("No se pudo generar totales_generales.xlsx", e);
        }
    }

    private CellStyle style(Workbook book, boolean bold) {
        CellStyle style = book.createCellStyle();
        Font font = book.createFont();
        font.setBold(bold);
        style.setFont(font);
        return style;
    }

    private void string(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value == null) {
            cell.setBlank();
        } else {
            cell.setCellValue(value);
        }
        cell.setCellStyle(style);
    }
}
