package um.haberes.report.reports.bono.infrastructure.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registro en memoria de los jobs de PDF. El servicio corre en una sola instancia, así que no
 * hace falta persistencia; los trabajos vencidos se expurgan al iniciar uno nuevo y se borra el
 * archivo asociado.
 */
@Component
@Slf4j
public class BonoPdfJobStore {

    private static final Duration TTL = Duration.ofMinutes(30);

    private final Map<String, BonoPdfJob> jobs = new ConcurrentHashMap<>();

    public BonoPdfJob crear() {
        expurgar();
        BonoPdfJob job = new BonoPdfJob(UUID.randomUUID().toString().replace("-", ""));
        jobs.put(job.id(), job);
        return job;
    }

    public Optional<BonoPdfJob> obtener(String jobId) {
        expurgar();
        return Optional.ofNullable(jobs.get(jobId));
    }

    /** Libera los trabajos terminados hace más de {@link #TTL}. */
    public void expurgar() {
        long limite = System.currentTimeMillis() - TTL.toMillis();
        jobs.entrySet().removeIf(entry -> vencido(entry.getValue(), limite));
    }

    private boolean vencido(BonoPdfJob job, long limite) {
        if (job.actualizado() >= limite) {
            return false;
        }
        borrarArchivo(job);
        return true;
    }

    private void borrarArchivo(BonoPdfJob job) {
        if (job.archivo() == null) {
            return;
        }
        try {
            Files.deleteIfExists(job.archivo().path());
        } catch (IOException e) {
            log.debug("No se pudo borrar el PDF del job {}: {}", job.id(), e.getMessage());
        }
    }
}
