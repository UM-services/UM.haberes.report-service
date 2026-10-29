package um.haberes.report.infrastructure.render.pdf;

import java.util.List;

/**
 * Encabezado institucional de un reporte. Reemplaza las 14 copias de {@code HeaderFooter}
 * que estaban repetidas en los services legacy.
 *
 * @param title             titulo centrado del reporte
 * @param period            periodo a mostrar ("9/2026"); {@code null} si no aplica
 * @param lines             lineas adicionales (facultad, sede, titulo inline, etc.)
 * @param logoPath          ruta del logo; {@code null} para encabezados sin imagen
 * @param state             leyenda centrada debajo del encabezado (ej. "Autorizados")
 * @param subtitle          linea centrada debajo del titulo
 * @param institution       nombre de la institucion en el bloque derecho
 * @param linesBeforePeriod {@code true} si las lineas van antes del periodo (encabezados
 *                          inline, donde el titulo tambien vive en {@link #lines()})
 */
public record PdfHeader(String title, String period, List<String> lines, String logoPath,
                        String state, String subtitle, String institution,
                        boolean linesBeforePeriod) {

    public PdfHeader {
        lines = lines == null ? List.of() : List.copyOf(lines);
    }

    public static PdfHeader of(String title, String period) {
        return new PdfHeader(title, period, List.of(), "marca_um.png", null, null,
                "UNIVERSIDAD DE MENDOZA", false);
    }

    public static PdfHeader of(String title, String period, List<String> lines) {
        return new PdfHeader(title, period, lines, "marca_um.png", null, null,
                "UNIVERSIDAD DE MENDOZA", false);
    }

    public static PdfHeader of(String title, String period, List<String> lines, String subtitle) {
        return new PdfHeader(title, period, lines, "marca_um.png", null, subtitle,
                "UNIVERSIDAD DE MENDOZA", false);
    }

    /** Encabezado con el titulo dentro del bloque derecho, antes del periodo. */
    public static PdfHeader inline(String institution, List<String> lines, String period,
                                   String subtitle) {
        return new PdfHeader("", period, lines, "marca_um.png", null, subtitle,
                institution, true);
    }

    public static PdfHeader withoutLogo(String title, String period) {
        return new PdfHeader(title, period, List.of(), null, null, null,
                "UNIVERSIDAD DE MENDOZA", false);
    }
}
