package um.haberes.report.reports.bono.infrastructure.job;

import um.haberes.report.reports.bono.domain.model.BonosDependenciaLoteResultado;
import um.haberes.report.shared.domain.model.ReportFile;

import java.util.List;

/**
 * Estado en memoria de una generación de PDF por dependencia. Se consulta por polling y se
 * descarta pasado el TTL del store.
 */
public class BonoPdfJob {

    public enum Estado {
        EN_PROCESO, LISTO, ERROR
    }

    private final String id;
    private final long creado = System.currentTimeMillis();
    private volatile Estado estado = Estado.EN_PROCESO;
    private volatile int procesados;
    private volatile int total;
    private volatile String mensaje;
    private volatile ReportFile archivo;
    private volatile List<BonosDependenciaLoteResultado.Fallido> fallidos = List.of();
    private volatile long actualizado = creado;

    public BonoPdfJob(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public Estado estado() {
        return estado;
    }

    public int procesados() {
        return procesados;
    }

    public int total() {
        return total;
    }

    public String mensaje() {
        return mensaje;
    }

    public ReportFile archivo() {
        return archivo;
    }

    public List<BonosDependenciaLoteResultado.Fallido> fallidos() {
        return fallidos;
    }

    public long actualizado() {
        return actualizado;
    }

    public void progreso(int procesados, int total) {
        this.procesados = procesados;
        if (total > 0) {
            this.total = total;
        }
        this.actualizado = System.currentTimeMillis();
    }

    public void listo(ReportFile archivo, List<BonosDependenciaLoteResultado.Fallido> fallidos) {
        this.archivo = archivo;
        this.fallidos = fallidos == null ? List.of() : List.copyOf(fallidos);
        this.procesados = this.total;
        this.estado = Estado.LISTO;
        this.actualizado = System.currentTimeMillis();
    }

    public void error(String mensaje) {
        this.mensaje = mensaje;
        this.estado = Estado.ERROR;
        this.actualizado = System.currentTimeMillis();
    }
}
