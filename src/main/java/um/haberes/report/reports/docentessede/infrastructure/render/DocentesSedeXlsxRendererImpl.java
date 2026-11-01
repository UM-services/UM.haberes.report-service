package um.haberes.report.reports.docentessede.infrastructure.render;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.xlsx.XlsxCells;
import um.haberes.report.reports.docentessede.domain.model.DocentesSedeContent;
import um.haberes.report.reports.docentessede.domain.ports.out.DocentesSedeXlsxRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class DocentesSedeXlsxRendererImpl implements DocentesSedeXlsxRenderer {

    private static final String SHEET_NAME = "Docentes por Sede";
    private static final List<String> HEADERS =
            List.of("Cargo", "Docente", "Designación", "Horas", "Desarraigo");

    @Override
    public void render(DocentesSedeContent content, Path target) {
        try (Workbook book = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(target)) {
            CellStyle normal = XlsxCells.style(book, false);
            CellStyle bold = XlsxCells.style(book, true);
            Sheet sheet = book.createSheet(SHEET_NAME);

            int fila = 0;
            Row title = sheet.createRow(fila);
            XlsxCells.text(title, 0, "Docentes por Sede", bold);
            Row subtitle = sheet.createRow(++fila);
            XlsxCells.text(subtitle, 0, content.facultad() + " · " + content.geografica()
                    + " · " + content.mes() + "/" + content.anho(), normal);
            ++fila;

            if (content.cursos().isEmpty()) {
                Row empty = sheet.createRow(fila);
                XlsxCells.text(empty, 0, "Sin datos para el período.", bold);
            }

            for (DocentesSedeContent.Curso curso : content.cursos()) {
                Row cursoRow = sheet.createRow(fila++);
                XlsxCells.text(cursoRow, 0,
                        curso.nombre() + (curso.periodo().isEmpty() ? "" : " · " + curso.periodo()),
                        bold);

                Row header = sheet.createRow(fila++);
                for (int column = 0; column < HEADERS.size(); column++) {
                    XlsxCells.text(header, column, HEADERS.get(column), bold);
                }
                for (DocentesSedeContent.Fila row : curso.filas()) {
                    Row data = sheet.createRow(fila++);
                    XlsxCells.text(data, 0, row.cargo(), normal);
                    XlsxCells.text(data, 1, row.docente(), normal);
                    XlsxCells.text(data, 2, row.designacion(), normal);
                    XlsxCells.text(data, 3, row.horas(), normal);
                    XlsxCells.text(data, 4, row.desarraigo(), normal);
                }
                fila++;
            }

            for (int column = 0; column < HEADERS.size(); column++) {
                sheet.autoSizeColumn(column);
            }
            book.write(output);
        } catch (IOException e) {
            throw new ReportException("No se pudo generar docentesSede.xlsx", e);
        }
    }
}
