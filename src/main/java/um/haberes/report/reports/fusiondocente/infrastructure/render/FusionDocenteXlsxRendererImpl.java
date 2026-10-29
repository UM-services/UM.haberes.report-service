package um.haberes.report.reports.fusiondocente.infrastructure.render;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.xlsx.XlsxCells;
import um.haberes.report.reports.fusiondocente.domain.model.FusionDocenteContent;
import um.haberes.report.reports.fusiondocente.domain.ports.out.FusionDocenteXlsxRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class FusionDocenteXlsxRendererImpl implements FusionDocenteXlsxRenderer {

    private static final String SHEET_NAME = "Fusión por Docente";
    private static final List<String> HEADERS =
            List.of("Legajo", "Apellido, Nombre", "Facultad", "Sede", "Cargo", "Básico");

    @Override
    public void render(FusionDocenteContent content, Path target) {
        try (Workbook book = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(target)) {
            CellStyle normal = XlsxCells.style(book, false);
            CellStyle bold = XlsxCells.style(book, true);
            Sheet sheet = book.createSheet(SHEET_NAME);

            int fila = 0;
            Row title = sheet.createRow(fila);
            XlsxCells.text(title, 0, "Fusión por Docente", bold);
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

            for (FusionDocenteContent.Fila row : content.filas()) {
                Row data = sheet.createRow(++fila);
                XlsxCells.text(data, 0, row.legajoId(), normal);
                XlsxCells.text(data, 1, row.apellidoNombre(), normal);
                XlsxCells.text(data, 2, row.facultad(), normal);
                XlsxCells.text(data, 3, row.sede(), normal);
                XlsxCells.text(data, 4, row.cargo(), normal);
                XlsxCells.text(data, 5, row.basico(), normal);
            }

            for (int column = 0; column < HEADERS.size(); column++) {
                sheet.autoSizeColumn(column);
            }
            book.write(output);
        } catch (IOException e) {
            throw new ReportException("No se pudo generar fusion.docente.xlsx", e);
        }
    }
}
