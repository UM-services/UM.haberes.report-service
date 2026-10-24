package um.haberes.report.contract;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica las reglas de arquitectura del plan (REFACTOR_HEXAGONAL_PLAN.md §4.6) escaneando
 * las fuentes, sin agregar dependencias.
 */
class HexagonalRulesTest {

    private static final Path MAIN = Path.of("src/main/java");
    private static final Pattern PACKAGE = Pattern.compile("^package\\s+([\\w.]+);", Pattern.MULTILINE);
    private static final Pattern IMPORT = Pattern.compile("^import\\s+(?:static\\s+)?([\\w.*]+);", Pattern.MULTILINE);
    private static final String SLICE_PREFIX = "um.haberes.report.reports.";
    private static final String SHARED_DOMAIN = "um.haberes.report.shared.domain.model";
    private static final List<String> LEGACY = List.of(
            "um.haberes.report.service", "um.haberes.report.controller",
            "um.haberes.report.model", "um.haberes.report.client", "um.haberes.report.util");

    @Test
    void losSlicesNoSeImportanEntreSi() throws IOException {
        List<String> violations = new ArrayList<>();
        for (Path file : sources()) {
            String source = Files.readString(file);
            String pkg = match(PACKAGE, source);
            if (pkg == null || !pkg.startsWith(SLICE_PREFIX)) {
                continue;
            }
            String slice = pkg.substring(SLICE_PREFIX.length()).split("\\.")[0];
            for (String imported : imports(source)) {
                if (!imported.startsWith(SLICE_PREFIX)) {
                    continue;
                }
                String other = imported.substring(SLICE_PREFIX.length()).split("\\.")[0];
                if (!other.equals(slice)) {
                    violations.add(file + " importa " + imported);
                }
            }
        }
        assertThat(violations).isEmpty();
    }

    @Test
    void sharedNoImportaReports() throws IOException {
        List<String> violations = new ArrayList<>();
        for (Path file : sources()) {
            String source = Files.readString(file);
            String pkg = match(PACKAGE, source);
            if (pkg == null || !pkg.startsWith("um.haberes.report.shared")) {
                continue;
            }
            for (String imported : imports(source)) {
                if (imported.startsWith(SLICE_PREFIX)) {
                    violations.add(file + " importa " + imported);
                }
            }
        }
        assertThat(violations).isEmpty();
    }

    @Test
    void elDominioNoImportaFrameworks() throws IOException {
        List<String> violations = new ArrayList<>();
        for (Path file : sources()) {
            String source = Files.readString(file);
            String pkg = match(PACKAGE, source);
            if (pkg == null || !pkg.contains(".domain")) {
                continue;
            }
            for (String imported : imports(source)) {
                if (imported.startsWith("java.") || imported.startsWith("lombok.")
                        || imported.startsWith("um.haberes.report.shared.domain")
                        || imported.startsWith("um.haberes.report.reports.")) {
                    continue;
                }
                violations.add(file + " importa " + imported);
            }
        }
        assertThat(violations).isEmpty();
    }

    @Test
    void laAplicacionNoImportaInfraestructuraNiLibreriasDeRender() throws IOException {
        List<String> violations = new ArrayList<>();
        for (Path file : sources()) {
            String source = Files.readString(file);
            String pkg = match(PACKAGE, source);
            if (pkg == null || !pkg.contains(".application")) {
                continue;
            }
            for (String imported : imports(source)) {
                if (imported.startsWith("um.haberes.report.infrastructure")
                        || imported.startsWith("org.openpdf")
                        || imported.startsWith("org.apache.poi")
                        || imported.startsWith("jakarta.mail")
                        || imported.startsWith("org.springframework.mail")
                        || imported.startsWith("org.springframework.cloud")) {
                    violations.add(file + " importa " + imported);
                }
            }
        }
        assertThat(violations).isEmpty();
    }

    @Test
    void unaSliceNoImportaControllerNiDtoDeOtra() throws IOException {
        List<String> violations = new ArrayList<>();
        for (Path file : sources()) {
            String source = Files.readString(file);
            String pkg = match(PACKAGE, source);
            if (pkg == null || !pkg.startsWith(SLICE_PREFIX) || !pkg.contains(".infrastructure.web.")) {
                continue;
            }
            for (String imported : imports(source)) {
                if (pkg.startsWith("um.haberes.report.reports.")
                        && (imported.startsWith("um.haberes.report.reports.")
                        && (imported.contains(".infrastructure.client")
                        || imported.contains(".infrastructure.web.dto")))) {
                    violations.add(file + " importa " + imported);
                }
            }
        }
        assertThat(violations).isEmpty();
    }

    @Test
    void noQuedanPaquetesLegacyNiAnotacionesProhibidas() throws IOException {
        List<String> violations = new ArrayList<>();
        for (Path file : sources()) {
            String source = Files.readString(file);
            for (String legacy : LEGACY) {
                if (match(PACKAGE, source) != null
                        && match(PACKAGE, source).startsWith(legacy)) {
                    violations.add(file + " vive en paquete legacy " + legacy);
                }
            }
            for (String annotation : List.of("@RequestParam", "@Query", "@ModelAttribute")) {
                if (source.contains(annotation)) {
                    violations.add(file + " usa " + annotation);
                }
            }
        }
        assertThat(violations).isEmpty();
    }

    private List<Path> sources() throws IOException {
        try (Stream<Path> files = Files.walk(MAIN)) {
            return files.filter(path -> path.toString().endsWith(".java")).toList();
        }
    }

    private String match(Pattern pattern, String source) {
        Matcher matcher = pattern.matcher(source);
        return matcher.find() ? matcher.group(1) : null;
    }

    private List<String> imports(String source) {
        List<String> values = new ArrayList<>();
        Matcher matcher = IMPORT.matcher(source);
        while (matcher.find()) {
            values.add(matcher.group(1));
        }
        return values;
    }
}
