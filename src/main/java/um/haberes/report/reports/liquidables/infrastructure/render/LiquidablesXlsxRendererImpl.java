package um.haberes.report.reports.liquidables.infrastructure.render;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.xlsx.XlsxCells;
import um.haberes.report.reports.liquidables.domain.model.LiquidablesContent;
import um.haberes.report.reports.liquidables.domain.ports.out.LiquidablesXlsxRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class LiquidablesXlsxRendererImpl implements LiquidablesXlsxRenderer {

    private static final String SHEET_NAME = "Liquidables";
    private static final List<String> HEADERS =
            List.of("Legajo", "Apellido, Nombre", "Estado", "Dependencia");

    @Override
    public void render(LiquidablesContent content, Path target) {
        try (Workbook book = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(target)) {
            CellStyle normal = XlsxCells.style(book, false);
            CellStyle bold = XlsxCells.style(book, true);
            Sheet sheet = book.createSheet(SHEET_NAME);

            int fila = 0;
            Row title = sheet.createRow(fila);
            XlsxCells.text(title, 0, "Planilla de Liquidables", bold);
            Row header = sheet.createRow(++fila);
            for (int column = 0; column < HEADERS.size(); column++) {
                XlsxCells.text(header, column, HEADERS.get(column), bold);
            }

            if (content.filas().isEmpty()) {
                Row empty = sheet.createRow(++fila);
                XlsxCells.text(empty, 0, "Sin datos.", bold);
            }

            for (LiquidablesContent.Fila row : content.filas()) {
                Row data = sheet.createRow(++fila);
                XlsxCells.decimal(data, 0, decimal(row.legajoId()), normal);
                XlsxCells.text(data, 1, row.apellidoNombre(), normal);
                XlsxCells.decimal(data, 2, decimal(row.estado()), normal);
                XlsxCells.text(data, 3, row.dependenciaNombre(), normal);
            }

            for (int column = 0; column < HEADERS.size(); column++) {
                sheet.autoSizeColumn(column);
            }
            book.write(output);
        } catch (IOException e) {
            throw new ReportException("No se pudo generar liquidables.xlsx", e);
        }
    }

    private java.math.BigDecimal decimal(Long value) {
        return value == null ? null : java.math.BigDecimal.valueOf(value);
    }

    private java.math.BigDecimal decimal(Integer value) {
        return value == null ? null : java.math.BigDecimal.valueOf(value);
    }
}
