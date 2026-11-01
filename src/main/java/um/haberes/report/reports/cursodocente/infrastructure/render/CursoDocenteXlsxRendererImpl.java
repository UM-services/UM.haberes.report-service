package um.haberes.report.reports.cursodocente.infrastructure.render;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.xlsx.XlsxCells;
import um.haberes.report.reports.cursodocente.domain.model.CursoDocenteContent;
import um.haberes.report.reports.cursodocente.domain.ports.out.CursoDocenteXlsxRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class CursoDocenteXlsxRendererImpl implements CursoDocenteXlsxRenderer {

    private static final String SHEET_NAME = "Cursos por Docente";
    private static final List<String> HEADERS = List.of(
            "Facultad", "Sede", "Espacio", "Horas", "Cargo", "Básico", "Anual", "Semestre 1",
            "Semestre 2");

    @Override
    public void render(CursoDocenteContent content, Path target) {
        try (Workbook book = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(target)) {
            CellStyle normal = XlsxCells.style(book, false);
            CellStyle bold = XlsxCells.style(book, true);
            Sheet sheet = book.createSheet(SHEET_NAME);

            int fila = 0;
            Row title = sheet.createRow(fila);
            XlsxCells.text(title, 0, "Cursos por Docente", bold);
            Row period = sheet.createRow(++fila);
            XlsxCells.text(period, 0, "Período: " + content.mes() + "/" + content.anho(), normal);
            ++fila;

            if (content.docentes().isEmpty()) {
                Row empty = sheet.createRow(fila);
                XlsxCells.text(empty, 0, "Sin datos para el período.", bold);
            }

            for (CursoDocenteContent.Docente docente : content.docentes()) {
                Row docenteRow = sheet.createRow(fila++);
                XlsxCells.text(docenteRow, 0,
                        docente.legajoId() + " - " + docente.apellidoNombre(), bold);

                Row header = sheet.createRow(fila++);
                for (int column = 0; column < HEADERS.size(); column++) {
                    XlsxCells.text(header, column, HEADERS.get(column), bold);
                }
                for (CursoDocenteContent.Fila row : docente.filas()) {
                    Row data = sheet.createRow(fila++);
                    XlsxCells.text(data, 0, row.facultad(), normal);
                    XlsxCells.text(data, 1, row.sede(), normal);
                    XlsxCells.text(data, 2, row.espacio(), normal);
                    XlsxCells.text(data, 3, row.horas(), normal);
                    XlsxCells.text(data, 4, row.cargo(), normal);
                    XlsxCells.text(data, 5, row.basico(), normal);
                    XlsxCells.text(data, 6, row.anual(), normal);
                    XlsxCells.text(data, 7, row.semestre1(), normal);
                    XlsxCells.text(data, 8, row.semestre2(), normal);
                }
                fila++;
            }

            for (int column = 0; column < HEADERS.size(); column++) {
                sheet.autoSizeColumn(column);
            }
            book.write(output);
        } catch (IOException e) {
            throw new ReportException("No se pudo generar curso-docente.xlsx", e);
        }
    }
}
