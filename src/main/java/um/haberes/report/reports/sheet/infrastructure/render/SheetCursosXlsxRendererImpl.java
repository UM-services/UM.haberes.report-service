package um.haberes.report.reports.sheet.infrastructure.render;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.sheet.domain.model.SheetCursosContent;
import um.haberes.report.reports.sheet.domain.ports.out.SheetCursosXlsxRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class SheetCursosXlsxRendererImpl implements SheetCursosXlsxRenderer {

    private static final List<String> HEADERS = List.of("Legajo", "Apellido, Nombre", "Periodo",
            "#Facultad", "Facultad", "#Sede", "Sede", "#Curso", "Curso", "#Cargo", "Cargo",
            "Horas", "Dictado");

    @Override
    public void render(SheetCursosContent content, Path target) {
        try (Workbook book = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(target)) {
            CellStyle normal = style(book, false);
            CellStyle bold = style(book, true);
            Sheet sheet = book.createSheet("Cursos");
            Row header = sheet.createRow(0);
            for (int column = 0; column < HEADERS.size(); column++) {
                string(header, column, HEADERS.get(column), bold);
            }

            int fila = 0;
            for (SheetCursosContent.Fila curso : content.filas()) {
                Row row = sheet.createRow(++fila);
                number(row, 0, curso.legajoId(), normal);
                string(row, 1, curso.apellidoNombre(), normal);
                string(row, 2, curso.periodo(), normal);
                number(row, 3, curso.facultadId(), normal);
                string(row, 4, curso.facultad(), normal);
                number(row, 5, curso.geograficaId(), normal);
                string(row, 6, curso.geografica(), normal);
                number(row, 7, curso.cursoId(), normal);
                string(row, 8, curso.curso(), normal);
                number(row, 9, curso.cargoTipoId(), normal);
                string(row, 10, curso.cargo(), normal);
                decimal(row, 11, curso.horas(), normal);
                string(row, 12, curso.dictado(), normal);
            }

            for (int column = 0; column < HEADERS.size(); column++) {
                sheet.autoSizeColumn(column);
            }
            book.write(output);
        } catch (IOException e) {
            throw new ReportException("No se pudo generar cursos.xlsx", e);
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

    private void number(Row row, int column, Number value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value == null) {
            cell.setBlank();
        } else {
            cell.setCellValue(value.doubleValue());
        }
        cell.setCellStyle(style);
    }

    private void decimal(Row row, int column, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value == null) {
            cell.setBlank();
        } else {
            cell.setCellValue(value.doubleValue());
        }
        cell.setCellStyle(style);
    }
}
