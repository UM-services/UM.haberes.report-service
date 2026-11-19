package um.haberes.report.reports.bono.infrastructure.job;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.bono.domain.model.BonosDependenciaLoteResultado;
import um.haberes.report.reports.bono.domain.ports.in.GenerateBonosDependenciaLoteUseCase;
import um.haberes.report.shared.application.exception.ReportBusinessException;
import um.haberes.report.shared.application.exception.ReportException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Arranca la generación del PDF en segundo plano y expone su estado. La request del navegador
 * vuelve enseguida con el id del job, así no depende de ningún timeout de proxy.
 */
@Component
public class BonoPdfJobService {

    /** {@code detail} del {@code ProblemDetail} con el que el core explica un error de negocio. */
    private static final Pattern DETAIL = Pattern.compile("\"detail\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");

    private final BonoPdfJobStore store;
    private final GenerateBonosDependenciaLoteUseCase useCase;
    private final TaskExecutor executor;

    public BonoPdfJobService(BonoPdfJobStore store,
                             GenerateBonosDependenciaLoteUseCase useCase,
                             @Qualifier("bonoPdfJobExecutor") TaskExecutor executor) {
        this.store = store;
        this.useCase = useCase;
        this.executor = executor;
    }

    public BonoPdfJob iniciar(Integer anho, Integer mes, Integer dependenciaId, String salida) {
        BonoPdfJob job = store.crear();
        executor.execute(() -> ejecutar(job, anho, mes, dependenciaId, salida));
        return job;
    }

    public BonoPdfJob obtener(String jobId) {
        return store.obtener(jobId).orElseThrow(() -> new ReportBusinessException(
                "El trabajo de generación no existe o expiró. Volvé a generar el PDF."));
    }

    private void ejecutar(BonoPdfJob job, Integer anho, Integer mes, Integer dependenciaId,
                          String salida) {
        try {
            BonosDependenciaLoteResultado resultado = useCase.generar(anho, mes, dependenciaId, salida,
                    job.id(), job::progreso);
            job.listo(resultado.archivo(), resultado.fallidos());
        } catch (FeignException e) {
            job.error(mensajeNegocio(e));
        } catch (ReportException e) {
            job.error(e.getMessage());
        } catch (RuntimeException e) {
            job.error("No se pudo generar el PDF consolidado.");
        }
    }

    /** Usa el detalle que devuelve el core; si no vino, deja el mensaje genérico. */
    private String mensajeNegocio(FeignException e) {
        String body = e.contentUTF8();
        if (body != null) {
            Matcher matcher = DETAIL.matcher(body);
            if (matcher.find()) {
                String detalle = matcher.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
                if (!detalle.isBlank()) {
                    return detalle;
                }
            }
        }
        return "No se pudo generar el PDF consolidado.";
    }
}
