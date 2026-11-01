package um.haberes.report.reports.detallecodigo.infrastructure.render;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.xlsx.XlsxCells;
import um.haberes.report.reports.detallecodigo.domain.model.DetalleCodigoContent;
import um.haberes.report.reports.detallecodigo.domain.ports.out.DetalleCodigoXlsxRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class DetalleCodigoXlsxRendererImpl implements DetalleCodigoXlsxRenderer {

    private static final List<String> HEADERS =
            List.of("Legajo", "Apellido, Nombre", "Liquidado", "Novedad", "Cantidad");

    @Override
    public void render(DetalleCodigoContent content, Path target) {
        try (Workbook book = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(target)) {
            CellStyle normal = XlsxCells.style(book, false);
            CellStyle bold = XlsxCells.style(book, true);
            Sheet sheet = book.createSheet("Código " + content.codigoId());

            int fila = 0;
            Row title = sheet.createRow(fila);
            XlsxCells.text(title, 0, title(content), bold);
            Row header = sheet.createRow(++fila);
            for (int column = 0; column < HEADERS.size(); column++) {
                XlsxCells.text(header, column, HEADERS.get(column), bold);
            }

            if (content.filas().isEmpty()) {
                Row empty = sheet.createRow(++fila);
                XlsxCells.text(empty, 0, "Sin datos para el período.", bold);
            }

            for (DetalleCodigoContent.Fila row : content.filas()) {
                Row data = sheet.createRow(++fila);
                XlsxCells.text(data, 0, row.legajoId(), normal);
                XlsxCells.text(data, 1, row.apellidoNombre(), normal);
                XlsxCells.text(data, 2, row.liquidado(), normal);
                XlsxCells.text(data, 3, row.novedad(), normal);
                XlsxCells.text(data, 4, row.cantidad(), normal);
            }

            for (int column = 0; column < HEADERS.size(); column++) {
                sheet.autoSizeColumn(column);
            }
            book.write(output);
        } catch (IOException e) {
            throw new ReportException("No se pudo generar detallecodigo.xlsx", e);
        }
    }

    private String title(DetalleCodigoContent content) {
        String nombre = content.codigoNombre();
        return nombre == null || nombre.isBlank()
                ? "Código " + content.codigoId()
                : "Código " + content.codigoId() + " · " + nombre;
    }
}
