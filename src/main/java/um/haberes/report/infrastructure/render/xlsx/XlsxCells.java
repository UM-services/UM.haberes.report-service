package um.haberes.report.infrastructure.render.xlsx;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.math.BigDecimal;

/** Helpers mínimos para escribir celdas con el mismo estilo en los XLSX de los reportes. */
public final class XlsxCells {

    private XlsxCells() {
    }

    public static CellStyle style(Workbook book, boolean bold) {
        CellStyle style = book.createCellStyle();
        Font font = book.createFont();
        font.setBold(bold);
        style.setFont(font);
        return style;
    }

    /** Escribe el valor como texto; null deja la celda en blanco. */
    public static void text(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value == null) {
            cell.setBlank();
        } else {
            cell.setCellValue(value.toString());
        }
        cell.setCellStyle(style);
    }

    /** Escribe el valor numérico; null deja la celda en blanco. */
    public static void decimal(Row row, int column, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value == null) {
            cell.setBlank();
        } else {
            cell.setCellValue(value.doubleValue());
        }
        cell.setCellStyle(style);
    }
}
