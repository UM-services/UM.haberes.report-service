package um.haberes.report.reports.excluidos.infrastructure.render;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import um.haberes.report.infrastructure.render.xlsx.XlsxCells;
import um.haberes.report.reports.excluidos.domain.model.ExcluidosContent;
import um.haberes.report.reports.excluidos.domain.ports.out.ExcluidosXlsxRenderer;
import um.haberes.report.shared.application.exception.ReportException;

@Component
public class ExcluidosXlsxRendererImpl implements ExcluidosXlsxRenderer {

    private static final String SHEET_NAME = "Excluidos";
    private static final List<String> HEADERS = List.of("Legajo", "Apellido, Nombre", "Importe",
            "Observaciones", "Fecha");

    @Override
    public void render(ExcluidosContent content, Path target) {
        try (Workbook book = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(target)) {
            CellStyle normal = XlsxCells.style(book, false);
            CellStyle bold = XlsxCells.style(book, true);
            Sheet sheet = book.createSheet(SHEET_NAME);

            int fila = 0;
            Row title = sheet.createRow(fila);
            XlsxCells.text(title, 0, "Excluidos", bold);
            Row period = sheet.createRow(++fila);
            XlsxCells.text(period, 0, "Período: " + content.mes() + "/" + content.anho(), normal);
            Row header = sheet.createRow(++fila);
            for (int column = 0; column < HEADERS.size(); column++) {
                XlsxCells.text(header, column, HEADERS.get(column), bold);
            }

            if (content.filas().isEmpty()) {
                Row empty = sheet.createRow(++fila);
                XlsxCells.text(empty, 0, "Sin excluidos para el período.", bold);
            }

            for (ExcluidosContent.Fila row : content.filas()) {
                Row data = sheet.createRow(++fila);
                XlsxCells.text(data, 0, row.legajoId(), normal);
                XlsxCells.text(data, 1, row.apellidoNombre(), normal);
                XlsxCells.text(data, 2, row.importe(), normal);
                XlsxCells.text(data, 3, row.observaciones(), normal);
                XlsxCells.text(data, 4, row.fecha(), normal);
            }

            for (int column = 0; column < HEADERS.size(); column++) {
                sheet.autoSizeColumn(column);
            }
            book.write(output);
        } catch (IOException e) {
            throw new ReportException("No se pudo generar excluidos.xlsx", e);
        }
    }
}
