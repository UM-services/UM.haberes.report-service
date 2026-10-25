package um.haberes.report.reports.cantidadcargos.infrastructure.render;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.xlsx.XlsxCells;
import um.haberes.report.reports.cantidadcargos.domain.model.CantidadCargosContent;
import um.haberes.report.reports.cantidadcargos.domain.ports.out.CantidadCargosXlsxRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class CantidadCargosXlsxRendererImpl implements CantidadCargosXlsxRenderer {

    private static final String SHEET_NAME = "Cantidad de Cargos";
    private static final List<String> HEADERS = List.of("Facultad", "Sede", "Legajo",
            "Apellido, Nombre", "Anuales", "Semestre 1", "Semestre 2", "Bruto");

    @Override
    public void render(CantidadCargosContent content, Path target) {
        try (Workbook book = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(target)) {
            CellStyle normal = XlsxCells.style(book, false);
            CellStyle bold = XlsxCells.style(book, true);
            Sheet sheet = book.createSheet(SHEET_NAME);

            int fila = 0;
            Row title = sheet.createRow(fila);
            XlsxCells.text(title, 0, "Cantidad de Cargos", bold);
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

            for (CantidadCargosContent.Fila row : content.filas()) {
                Row data = sheet.createRow(++fila);
                XlsxCells.text(data, 0, row.facultad(), normal);
                XlsxCells.text(data, 1, row.sede(), normal);
                XlsxCells.text(data, 2, row.legajoId(), normal);
                XlsxCells.text(data, 3, row.apellidoNombre(), normal);
                XlsxCells.text(data, 4, row.anuales(), normal);
                XlsxCells.text(data, 5, row.semestre1(), normal);
                XlsxCells.text(data, 6, row.semestre2(), normal);
                XlsxCells.text(data, 7, row.bruto(), normal);
            }

            for (int column = 0; column < HEADERS.size(); column++) {
                sheet.autoSizeColumn(column);
            }
            book.write(output);
        } catch (IOException e) {
            throw new ReportException("No se pudo generar cantidadcargos.xlsx", e);
        }
    }
}
