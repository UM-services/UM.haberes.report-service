package um.haberes.report.shared.domain.model;

import java.nio.file.Path;

/** Archivo generado por un reporte, listo para entregarse por HTTP. */
public record ReportFile(Path path, String displayName, Format format, long size) {

    public enum Format {
        PDF, XLSX
    }
}
