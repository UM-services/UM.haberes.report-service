package um.haberes.report.shared.infrastructure.web;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import um.haberes.report.shared.application.exception.ReportBusinessException;

/**
 * Traduce los errores de negocio de los reportes a 400 con {@link ProblemDetail}, para que el
 * frontend muestre el mensaje real ("No hay liquidaciones para la dependencia…") en lugar de un
 * 500 genérico. Las fallas técnicas ({@code ReportException}) se dejan como 500.
 *
 * <p>Los 404 de los servicios de origen (por ejemplo una facultad inexistente) se devuelven como
 * 404 y el resto de las fallas de Feign como 502, en lugar de un 500 sin detalle.
 */
@RestControllerAdvice
@Slf4j
public class ReportExceptionHandler {

    @ExceptionHandler(ReportBusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusiness(ReportBusinessException exception) {
        log.warn("Error de negocio en un reporte: {}", exception.getMessage());
        return problem(HttpStatus.BAD_REQUEST, "No se pudo generar el reporte",
                exception.getMessage());
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ProblemDetail> handleNotFound(FeignException.NotFound exception) {
        log.warn("El servicio de origen no encontró el recurso: {}", exception.getMessage());
        return problem(HttpStatus.NOT_FOUND, "Recurso no encontrado",
                "No existe el registro solicitado en el servicio de origen.");
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ProblemDetail> handleFeign(FeignException exception) {
        log.error("Falla al consultar el servicio de origen: {}", exception.getMessage());
        return problem(HttpStatus.BAD_GATEWAY, "Error del servicio de origen",
                "El servicio de origen no respondió correctamente.");
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return ResponseEntity.status(status).body(problem);
    }
}
