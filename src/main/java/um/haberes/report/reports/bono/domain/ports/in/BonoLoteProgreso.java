package um.haberes.report.reports.bono.domain.ports.in;

/** Avance del lote hacia quien lo esté mirando (job asincrónico). */
@FunctionalInterface
public interface BonoLoteProgreso {

    void avanzar(int procesados, int total);
}
