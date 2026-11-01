package um.haberes.report.shared.application.exception;

/** Falla al generar o entregar un reporte. Se traduce a HTTP 500 en infrastructure/web. */
public class ReportException extends RuntimeException {

    public ReportException(String message) {
        super(message);
    }

    public ReportException(String message, Throwable cause) {
        super(message, cause);
    }
}
