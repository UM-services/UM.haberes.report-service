package um.haberes.report.reports.cargosclaseperiodo.infrastructure.render;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import um.haberes.report.infrastructure.render.xlsx.XlsxCells;
import um.haberes.report.reports.cargosclaseperiodo.domain.model.CargosClasePeriodoContent;
import um.haberes.report.reports.cargosclaseperiodo.domain.ports.out.CargosClasePeriodoXlsxRenderer;
import um.haberes.report.shared.application.exception.ReportException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class CargosClasePeriodoXlsxRendererImpl implements CargosClasePeriodoXlsxRenderer {

    private static final String SHEET_NAME = "Cargos con Clase";
    private static final List<String> HEADERS = List.of(
            "Legajo", "Apellido, Nombre", "Cargo", "Descripción", "Desde", "Hasta", "Horas",
            "Clase");

    @Override
    public void render(CargosClasePeriodoContent content, Path target) {
        try (Workbook book = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(target)) {
            CellStyle normal = XlsxCells.style(book, false);
            CellStyle bold = XlsxCells.style(book, true);
            Sheet sheet = book.createSheet(SHEET_NAME);

            int fila = 0;
            Row title = sheet.createRow(fila);
            XlsxCells.text(title, 0, "Cargos con Clase por Facultad", bold);
            Row subtitle = sheet.createRow(++fila);
            XlsxCells.text(subtitle, 0, content.facultad() + " · "
                    + content.mes() + "/" + content.anho(), normal);
            ++fila;

            if (content.sedes().isEmpty()) {
                Row empty = sheet.createRow(fila);
                XlsxCells.text(empty, 0, "Sin datos para el período.", bold);
            }

            for (CargosClasePeriodoContent.Sede sede : content.sedes()) {
                Row sedeRow = sheet.createRow(fila++);
                XlsxCells.text(sedeRow, 0, "Sede: " + sede.nombre(), bold);

                Row header = sheet.createRow(fila++);
                for (int column = 0; column < HEADERS.size(); column++) {
                    XlsxCells.text(header, column, HEADERS.get(column), bold);
                }
                for (CargosClasePeriodoContent.Fila row : sede.filas()) {
                    Row data = sheet.createRow(fila++);
                    XlsxCells.text(data, 0, row.legajoId(), normal);
                    XlsxCells.text(data, 1, row.apellidoNombre(), normal);
                    XlsxCells.text(data, 2, row.cargo(), normal);
                    XlsxCells.text(data, 3, row.descripcion(), normal);
                    XlsxCells.text(data, 4, row.periodoDesde(), normal);
                    XlsxCells.text(data, 5, row.periodoHasta(), normal);
                    XlsxCells.text(data, 6, row.horas(), normal);
                    XlsxCells.text(data, 7, row.clase(), normal);
                }
                fila++;
            }

            for (int column = 0; column < HEADERS.size(); column++) {
                sheet.autoSizeColumn(column);
            }
            book.write(output);
        } catch (IOException e) {
            throw new ReportException("No se pudo generar cargos-clase-periodo.xlsx", e);
        }
    }
}
