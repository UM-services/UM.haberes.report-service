package um.haberes.report.support;

import org.openpdf.text.pdf.PdfReader;
import org.openpdf.text.pdf.parser.PdfTextExtractor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Congela el contenido de los reportes como texto extraido.
 *
 * <p>Dos carpetas con proposito distinto:
 * <ul>
 *   <li>{@code legacy-baseline/}: salida del codigo <b>antes</b> del refactor. Se escribe una
 *       sola vez (F0) y no se vuelve a tocar. Es la evidencia de "no se perdio nada".</li>
 *   <li>{@code golden/}: salida esperada <b>vigente</b>. Arranca igual al baseline; solo cambia
 *       cuando se aplica una correccion declarada en {@code expected-fixes.yml}.</li>
 * </ul>
 *
 * <p>Modo escritura (solo F0): {@code -Dgolden.write=true -Dbaseline.write=true}.
 * En ejecucion normal compara y falla con un diff legible.
 */
public final class GoldenFiles {

    private static final Path GOLDEN = Path.of("src/test/resources/golden");
    private static final Path BASELINE = Path.of("src/test/resources/legacy-baseline");
    private static final boolean WRITE_GOLDEN = Boolean.getBoolean("golden.write");
    private static final boolean WRITE_BASELINE = Boolean.getBoolean("baseline.write");

    private GoldenFiles() {
    }

    public static void assertPdf(String report, String scenario, byte[] pdf) {
        compare(report, scenario, extractPdfText(pdf));
    }

    public static void assertWorkbook(String report, String scenario, byte[] xlsx) {
        compare(report, scenario, extractWorkbook(xlsx));
    }

    /** Dump determinista de un XLSX: hoja, fila, columna, tipo y valor formateado. */
    public static String extractWorkbook(byte[] xlsx) {
        try (org.apache.poi.ss.usermodel.Workbook workbook =
                     org.apache.poi.ss.usermodel.WorkbookFactory.create(new ByteArrayInputStream(xlsx))) {
            org.apache.poi.ss.usermodel.DataFormatter formatter =
                    new org.apache.poi.ss.usermodel.DataFormatter(java.util.Locale.ROOT);
            StringBuilder out = new StringBuilder();
            for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(index);
                out.append("SHEET ").append(sheet.getSheetName()).append('\n');
                for (org.apache.poi.ss.usermodel.Row row : sheet) {
                    out.append('R').append(row.getRowNum()).append(':');
                    for (org.apache.poi.ss.usermodel.Cell cell : row) {
                        out.append('[')
                                .append(cell.getColumnIndex()).append('|')
                                .append(cell.getCellType()).append('|')
                                .append(formatter.formatCellValue(cell))
                                .append(']');
                    }
                    out.append('\n');
                }
            }
            return normalize(out.toString());
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el XLSX generado", e);
        }
    }

    public static void assertText(String report, String scenario, String text) {
        compare(report, scenario, normalize(text));
    }

    public static String extractPdfText(byte[] pdf) {
        PdfReader reader = null;
        try {
            reader = new PdfReader(new ByteArrayInputStream(pdf));
            PdfTextExtractor extractor = new PdfTextExtractor(reader);
            List<String> pages = new ArrayList<>();
            for (int page = 1; page <= reader.getNumberOfPages(); page++) {
                pages.add(extractor.getTextFromPage(page));
            }
            return normalize(String.join("\n\f\n", pages));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el PDF generado", e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static String normalize(String text) {
        String normalized = text.replace("\r\n", "\n").replace('\r', '\n');
        StringBuilder out = new StringBuilder(normalized.length());
        for (String line : normalized.split("\n", -1)) {
            out.append(line.replaceAll("[ \\t]+$", "")).append('\n');
        }
        return out.toString().replaceAll("\n+$", "");
    }

    private static void compare(String report, String scenario, String actual) {
        String expected = read(GOLDEN, report, scenario);
        if (WRITE_GOLDEN || expected == null) {
            write(GOLDEN, report, scenario, actual);
            expected = actual;
        }
        if (WRITE_BASELINE) {
            write(BASELINE, report, scenario, actual);
        }
        if (!expected.equals(actual)) {
            throw new AssertionError(diff(report, scenario, expected, actual));
        }
    }

    private static String read(Path root, String report, String scenario) {
        Path file = root.resolve(report).resolve(scenario + ".txt");
        return Files.exists(file) ? normalize(readFile(file)) : null;
    }

    private static String readFile(Path file) {
        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + file, e);
        }
    }

    private static void write(Path root, String report, String scenario, String content) {
        Path file = root.resolve(report).resolve(scenario + ".txt");
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, content + "\n", StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir " + file, e);
        }
    }

    private static String diff(String report, String scenario, String expected, String actual) {
        Path actualFile = Path.of("target/golden-actual").resolve(report).resolve(scenario + ".txt");
        try {
            Files.createDirectories(actualFile.getParent());
            Files.writeString(actualFile, actual + "\n", StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // el diff en memoria alcanza
        }
        String[] expectedLines = expected.split("\n", -1);
        String[] actualLines = actual.split("\n", -1);
        StringBuilder out = new StringBuilder()
                .append("Contenido distinto en ").append(report).append('/').append(scenario)
                .append("\n  esperado: src/test/resources/golden/").append(report).append('/').append(scenario).append(".txt")
                .append("\n  actual:   ").append(actualFile)
                .append("\n");
        int max = Math.max(expectedLines.length, actualLines.length);
        int shown = 0;
        for (int i = 0; i < max && shown < 40; i++) {
            String exp = i < expectedLines.length ? expectedLines[i] : "<falta>";
            String act = i < actualLines.length ? actualLines[i] : "<falta>";
            if (!exp.equals(act)) {
                out.append("  linea ").append(i + 1).append(":\n")
                        .append("    - ").append(exp).append('\n')
                        .append("    + ").append(act).append('\n');
                shown++;
            }
        }
        if (shown == 40) {
            out.append("  ... (diff truncado)\n");
        }
        return out.toString();
    }
}
