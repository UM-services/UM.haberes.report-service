package um.haberes.report.shared.infrastructure.file;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.application.exception.ReportException;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Adaptador de disco para los reportes generados. La carpeta sale de {@code path.files},
 * igual que en el codigo legacy.
 */
@Component
@RequiredArgsConstructor
public class ReportStorageAdapter implements ReportStorage {

    private final Environment environment;

    @Override
    public Path resolve(String fileName) {
        String base = environment.getProperty("path.files", "");
        Path directory = Path.of(base);
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new ReportException("No se pudo preparar la carpeta de reportes: " + directory, e);
        }
        return directory.resolve(fileName);
    }

    @Override
    public ReportFile load(Path path, String displayName, ReportFile.Format format) {
        try {
            if (!Files.isRegularFile(path)) {
                throw new ReportException("No se pudo generar el reporte " + displayName);
            }
            return new ReportFile(path, displayName, format, Files.size(path));
        } catch (IOException e) {
            throw new ReportException("No se pudo leer el reporte " + displayName, e);
        }
    }
}
