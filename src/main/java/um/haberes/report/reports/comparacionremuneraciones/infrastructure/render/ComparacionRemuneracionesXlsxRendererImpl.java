package um.haberes.report.reports.comparacionremuneraciones.infrastructure.render;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.xlsx.XlsxCells;
import um.haberes.report.reports.comparacionremuneraciones.domain.model.ComparacionRemuneracionesContent;
import um.haberes.report.reports.comparacionremuneraciones.domain.ports.out.ComparacionRemuneracionesXlsxRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class ComparacionRemuneracionesXlsxRendererImpl implements ComparacionRemuneracionesXlsxRenderer {

    private static final String SHEET_NAME = "Comparación Remuneraciones";
    private static final List<String> HEADERS = List.of(
            "Legajo", "Apellido, Nombre", "Bruto Anterior", "Bruto Actual", "Diferencia Bruto",
            "%", "Neto Anterior", "Neto Actual", "Diferencia Neto");

    @Override
    public void render(ComparacionRemuneracionesContent content, Path target) {
        try (Workbook book = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(target)) {
            CellStyle normal = XlsxCells.style(book, false);
            CellStyle bold = XlsxCells.style(book, true);
            Sheet sheet = book.createSheet(SHEET_NAME);

            int fila = 0;
            Row title = sheet.createRow(fila);
            XlsxCells.text(title, 0, "Comparación de Remuneraciones", bold);
            Row period = sheet.createRow(++fila);
            XlsxCells.text(period, 0, "Período: " + content.mes() + "/" + content.anho()
                    + "  ·  Anterior: " + content.mesAnterior() + "/" + content.anhoAnterior(), normal);
            ++fila;

            if (content.dependencias().isEmpty()) {
                Row empty = sheet.createRow(fila);
                XlsxCells.text(empty, 0, "Sin datos para el período.", bold);
            }

            for (ComparacionRemuneracionesContent.Dependencia dependencia : content.dependencias()) {
                Row group = sheet.createRow(fila++);
                XlsxCells.text(group, 0, dependencia.nombre(), bold);

                Row header = sheet.createRow(fila++);
                for (int column = 0; column < HEADERS.size(); column++) {
                    XlsxCells.text(header, column, HEADERS.get(column), bold);
                }

                for (ComparacionRemuneracionesContent.Fila row : dependencia.filas()) {
                    Row data = sheet.createRow(fila++);
                    XlsxCells.text(data, 0, row.legajoId(), normal);
                    XlsxCells.text(data, 1, row.apellidoNombre(), normal);
                    XlsxCells.text(data, 2, row.brutoAnterior(), normal);
                    XlsxCells.text(data, 3, row.brutoActual(), normal);
                    XlsxCells.text(data, 4, row.brutoDiferencia(), normal);
                    XlsxCells.text(data, 5, row.brutoPorcentaje(), normal);
                    XlsxCells.text(data, 6, row.netoAnterior(), normal);
                    XlsxCells.text(data, 7, row.netoActual(), normal);
                    XlsxCells.text(data, 8, row.netoDiferencia(), normal);
                }
                fila++;
            }

            for (int column = 0; column < HEADERS.size(); column++) {
                sheet.autoSizeColumn(column);
            }
            book.write(output);
        } catch (IOException e) {
            throw new ReportException("No se pudo generar comparacion-remuneraciones.xlsx", e);
        }
    }
}
