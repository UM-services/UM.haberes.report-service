package um.haberes.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import um.haberes.report.client.haberes.core.*;
import um.haberes.report.kotlin.dto.haberes.core.*;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SheetService {

    private final Environment environment;
    private final FacultadClient facultadClient;
    private final GeograficaClient geograficaClient;
    private final CursoClient cursoClient;
    private final CargoTipoClient cargoTipoClient;
    private final CursoCargoClient cursoCargoClient;
    private final PersonaClient personaClient;

    public String generateCursos(Integer anho, Integer mes) {
        String path = environment.getProperty("path.files");

        String filename = path + "cursos" + anho + String.format("%02d", mes) + ".xlsx";

        Workbook book = new XSSFWorkbook();
        CellStyle styleNormal = book.createCellStyle();
        Font fontNormal = book.createFont();
        fontNormal.setBold(false);
        styleNormal.setFont(fontNormal);

        CellStyle styleBold = book.createCellStyle();
        Font fontBold = book.createFont();
        fontBold.setBold(true);
        styleBold.setFont(fontBold);

        Sheet sheet = book.createSheet("Cursos");
        Row row = null;
        int fila = 0;
        row = sheet.createRow(fila);
        this.setCellString(row, 0, "Legajo", styleBold);
        this.setCellString(row, 1, "Apellido, Nombre", styleBold);
        this.setCellString(row, 2, "Periodo", styleBold);
        this.setCellString(row, 3, "#Facultad", styleBold);
        this.setCellString(row, 4, "Facultad", styleBold);
        this.setCellString(row, 5, "#Sede", styleBold);
        this.setCellString(row, 6, "Sede", styleBold);
        this.setCellString(row, 7, "#Curso", styleBold);
        this.setCellString(row, 8, "Curso", styleBold);
        this.setCellString(row, 9, "#Cargo", styleBold);
        this.setCellString(row, 10, "Cargo", styleBold);
        this.setCellString(row, 11, "Horas", styleBold);
        this.setCellString(row, 12, "Dictado", styleBold);

        Map<Integer, FacultadDto> facultades = facultadClient.findAll().stream()
                .collect(Collectors.toMap(FacultadDto::getFacultadId, facultad -> facultad));

        Map<Integer, GeograficaDto> geograficas = geograficaClient.findAll().stream()
                .collect(Collectors.toMap(GeograficaDto::getGeograficaId, geografica -> geografica));

        Map<Long, CursoDto> cursos = cursoClient.findAll().stream()
                .collect(Collectors.toMap(CursoDto::getCursoId, curso -> curso));

        Map<Integer, CargoTipoDto> cargos = cargoTipoClient.findAll().stream()
                .collect(Collectors.toMap(CargoTipoDto::getCargoTipoId, cargoTipo -> cargoTipo));

        List<Long> legajoIds = cursoCargoClient.findAllByPeriodo(anho, mes).stream()
                .map(CursoCargoDto::getLegajoId).collect(Collectors.toList());

        for (PersonaDto persona : personaClient.findAllLegajos(legajoIds)) {
            for (CursoCargoDto cursoCargo : cursoCargoClient.findAllByLegajo(persona.getLegajoId(), anho, mes)) {
                CursoDto curso = cursos.get(cursoCargo.getCursoId());
                FacultadDto facultad = facultades.get(curso.getFacultadId());
                GeograficaDto geografica = geograficas.get(curso.getGeograficaId());
                CargoTipoDto cargoTipo = cargos.get(cursoCargo.getCargoTipoId());
                row = sheet.createRow(++fila);
                this.setCellLong(row, 0, cursoCargo.getLegajoId(), styleNormal);
                this.setCellString(row, 1, persona.getApellido() + ", " + persona.getNombre(), styleNormal);
                this.setCellString(row, 2, mes + "/" + anho, styleNormal);
                this.setCellInteger(row, 3, curso.getFacultadId(), styleNormal);
                this.setCellString(row, 4, facultad.getNombre(), styleNormal);
                this.setCellInteger(row, 5, curso.getGeograficaId(), styleNormal);
                this.setCellString(row, 6, geografica.getNombre(), styleNormal);
                this.setCellLong(row, 7, cursoCargo.getCursoId(), styleNormal);
                this.setCellString(row, 8, curso.getNombre(), styleNormal);
                this.setCellInteger(row, 9, cursoCargo.getCargoTipoId(), styleNormal);
                this.setCellString(row, 10, cargoTipo.getNombre(), styleNormal);
                this.setCellBigDecimal(row, 11, cursoCargo.getHorasSemanales(), styleNormal);
                String dictado = "";
                if (curso.getAnual() == 1)
                    dictado = "Anual";
                if (curso.getSemestre1() == 1)
                    dictado = "1er Semestre";
                if (curso.getSemestre2() == 1)
                    dictado = "2do Semestre";
                this.setCellString(row, 12, dictado, styleNormal);
            }
        }

        for (int column = 0; column < sheet.getRow(0).getPhysicalNumberOfCells(); column++)
            sheet.autoSizeColumn(column);

        try {
            File file = new File(filename);
            FileOutputStream output = new FileOutputStream(file);
            book.write(output);
            output.flush();
            output.close();
            log.debug(file.getAbsolutePath());
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }

    private void setCellOffsetDateTime(Row row, int column, OffsetDateTime value, CellStyle style) {
        setCellString(row, column, DateTimeFormatter.ofPattern("dd-MM-yyyy").format(value), style);
    }

    private void setCellBigDecimal(Row row, int column, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value.doubleValue());
        cell.setCellStyle(style);
    }

    private void setCellString(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void setCellLong(Row row, int column, Long value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void setCellInteger(Row row, int column, Integer value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void setCellByte(Row row, int column, Byte value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

}
