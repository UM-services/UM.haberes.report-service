package um.haberes.report.reports.basicosperiodo.infrastructure.render;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.xlsx.XlsxCells;
import um.haberes.report.reports.basicosperiodo.domain.model.BasicosPeriodoContent;
import um.haberes.report.reports.basicosperiodo.domain.ports.out.BasicosPeriodoXlsxRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class BasicosPeriodoXlsxRendererImpl implements BasicosPeriodoXlsxRenderer {

    private static final String SHEET_NAME = "Básicos";
    private static final List<String> HEADERS = List.of("Legajo", "Apellido, Nombre",
            "Dependencia", "Clase / Código", "Categoría / Cargo", "Básico / Valor hora",
            "Horas jornada", "Jornada / Horas");

    @Override
    public void render(BasicosPeriodoContent content, Path target) {
        try (Workbook book = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(target)) {
            CellStyle normal = XlsxCells.style(book, false);
            CellStyle bold = XlsxCells.style(book, true);
            Sheet sheet = book.createSheet(SHEET_NAME);

            int fila = 0;
            Row title = sheet.createRow(fila);
            XlsxCells.text(title, 0, "Básicos por Período", bold);
            Row period = sheet.createRow(++fila);
            XlsxCells.text(period, 0, "Período: " + content.mes() + "/" + content.anho(), normal);
            Row header = sheet.createRow(++fila);
            for (int column = 0; column < HEADERS.size(); column++) {
                XlsxCells.text(header, column, HEADERS.get(column), bold);
            }

            if (content.filas().isEmpty()) {
                Row empty = sheet.createRow(++fila);
                XlsxCells.text(empty, 0, "Sin datos para el período.", bold);
            }

            for (BasicosPeriodoContent.Fila row : content.filas()) {
                Row data = sheet.createRow(++fila);
                XlsxCells.text(data, 0, row.legajoId(), normal);
                XlsxCells.text(data, 1, row.apellidoNombre(), normal);
                XlsxCells.text(data, 2, row.dependencia(), normal);
                XlsxCells.text(data, 3, row.claseCodigo(), normal);
                XlsxCells.text(data, 4, row.categoriaCargo(), normal);
                XlsxCells.text(data, 5, row.basico(), normal);
                XlsxCells.text(data, 6, row.horasJornada(), normal);
                XlsxCells.text(data, 7, row.jornada(), normal);
            }

            for (int column = 0; column < HEADERS.size(); column++) {
                sheet.autoSizeColumn(column);
            }
            book.write(output);
        } catch (IOException e) {
            throw new ReportException("No se pudo generar basicosperiodo.xlsx", e);
        }
    }
}
