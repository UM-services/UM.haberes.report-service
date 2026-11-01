package um.haberes.report.shared.application.exception;

/**
 * Error de negocio de un reporte (falta un dato de entrada, no hay filas para el período, el
 * catálogo pedido no existe). Se traduce a <b>400</b> con el mensaje, a diferencia de
 * {@link ReportException}, que representa una falla técnica de generación (500).
 */
public class ReportBusinessException extends ReportException {

    public ReportBusinessException(String message) {
        super(message);
    }
}
