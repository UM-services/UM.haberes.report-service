package um.haberes.report.reports.designacionesdocente.infrastructure.render;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.xlsx.XlsxCells;
import um.haberes.report.reports.designacionesdocente.domain.model.DesignacionesDocenteContent;
import um.haberes.report.reports.designacionesdocente.domain.ports.out.DesignacionesDocenteXlsxRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class DesignacionesDocenteXlsxRendererImpl implements DesignacionesDocenteXlsxRenderer {

    private static final String SHEET_NAME = "Designaciones Docente";
    private static final List<String> HEADERS = List.of(
            "Facultad", "Sede", "Espacio", "Horas", "Cargo", "Designación", "Horas designación",
            "Dictado");

    @Override
    public void render(DesignacionesDocenteContent content, Path target) {
        try (Workbook book = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(target)) {
            CellStyle normal = XlsxCells.style(book, false);
            CellStyle bold = XlsxCells.style(book, true);
            Sheet sheet = book.createSheet(SHEET_NAME);

            int fila = 0;
            Row title = sheet.createRow(fila);
            XlsxCells.text(title, 0, "Designación de Docentes", bold);
            Row period = sheet.createRow(++fila);
            XlsxCells.text(period, 0, "Período: " + content.mes() + "/" + content.anho(), normal);
            ++fila;

            if (content.docentes().isEmpty()) {
                Row empty = sheet.createRow(fila);
                XlsxCells.text(empty, 0, "Sin datos para el período.", bold);
            }

            for (DesignacionesDocenteContent.Docente docente : content.docentes()) {
                Row docenteRow = sheet.createRow(fila++);
                XlsxCells.text(docenteRow, 0,
                        docente.legajoId() + " - " + docente.apellidoNombre(), bold);

                Row header = sheet.createRow(fila++);
                for (int column = 0; column < HEADERS.size(); column++) {
                    XlsxCells.text(header, column, HEADERS.get(column), bold);
                }
                for (DesignacionesDocenteContent.Fila row : docente.filas()) {
                    Row data = sheet.createRow(fila++);
                    XlsxCells.text(data, 0, row.facultad(), normal);
                    XlsxCells.text(data, 1, row.sede(), normal);
                    XlsxCells.text(data, 2, row.espacio(), normal);
                    XlsxCells.text(data, 3, row.horas(), normal);
                    XlsxCells.text(data, 4, row.cargo(), normal);
                    XlsxCells.text(data, 5, row.designacion(), normal);
                    XlsxCells.text(data, 6, row.horasDesignacion(), normal);
                    XlsxCells.text(data, 7, row.dictado(), normal);
                }
                fila++;
            }

            for (int column = 0; column < HEADERS.size(); column++) {
                sheet.autoSizeColumn(column);
            }
            book.write(output);
        } catch (IOException e) {
            throw new ReportException("No se pudo generar designaciones-docente.xlsx", e);
        }
    }
}
