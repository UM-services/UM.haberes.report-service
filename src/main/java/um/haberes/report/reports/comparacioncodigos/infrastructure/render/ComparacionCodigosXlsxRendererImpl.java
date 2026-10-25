package um.haberes.report.reports.comparacioncodigos.infrastructure.render;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.xlsx.XlsxCells;
import um.haberes.report.reports.comparacioncodigos.domain.model.ComparacionCodigosContent;
import um.haberes.report.reports.comparacioncodigos.domain.ports.out.ComparacionCodigosXlsxRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class ComparacionCodigosXlsxRendererImpl implements ComparacionCodigosXlsxRenderer {

    private static final String SHEET_NAME = "Comparación de Códigos";
    private static final List<String> HEADERS =
            List.of("Código", "Descripción", "Novedades", "Liquidado", "Diferencia");

    @Override
    public void render(Integer anho, Integer mes, ComparacionCodigosContent content, Path target) {
        try (Workbook book = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(target)) {
            CellStyle normal = XlsxCells.style(book, false);
            CellStyle bold = XlsxCells.style(book, true);
            Sheet sheet = book.createSheet(SHEET_NAME);

            int fila = 0;
            Row title = sheet.createRow(fila);
            XlsxCells.text(title, 0, "Comparación de Códigos", bold);
            Row period = sheet.createRow(++fila);
            XlsxCells.text(period, 0, "Período: " + mes + "/" + anho, normal);
            Row header = sheet.createRow(++fila);
            for (int column = 0; column < HEADERS.size(); column++) {
                XlsxCells.text(header, column, HEADERS.get(column), bold);
            }

            if (content.filas().isEmpty()) {
                Row empty = sheet.createRow(++fila);
                XlsxCells.text(empty, 0, "Sin datos para el período.", bold);
            }

            for (ComparacionCodigosContent.Fila row : content.filas()) {
                Row data = sheet.createRow(++fila);
                XlsxCells.text(data, 0, row.codigoId(), normal);
                XlsxCells.text(data, 1, row.nombre(), normal);
                XlsxCells.text(data, 2, row.novedades(), normal);
                XlsxCells.text(data, 3, row.liquidado(), normal);
                XlsxCells.text(data, 4, row.diferencia(), normal);
            }

            for (int column = 0; column < HEADERS.size(); column++) {
                sheet.autoSizeColumn(column);
            }
            book.write(output);
        } catch (IOException e) {
            throw new ReportException("No se pudo generar comparacion_codigos.xlsx", e);
        }
    }
}
