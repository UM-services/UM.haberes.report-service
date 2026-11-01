package um.haberes.report.shared.domain.ports.out;

import um.haberes.report.shared.domain.model.ReportFile;

import java.nio.file.Path;

/** Resuelve la ruta de salida y expone el archivo generado. */
public interface ReportStorage {

    Path resolve(String fileName);

    ReportFile load(Path path, String displayName, ReportFile.Format format);
}
