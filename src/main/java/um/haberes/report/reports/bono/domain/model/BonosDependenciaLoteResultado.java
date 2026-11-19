package um.haberes.report.reports.bono.domain.model;

import um.haberes.report.shared.domain.model.ReportFile;

import java.util.List;

/** Resultado del lote: el PDF consolidado de los legajos que se pudieron generar. */
public record BonosDependenciaLoteResultado(ReportFile archivo, int total, List<Fallido> fallidos) {

    public BonosDependenciaLoteResultado {
        fallidos = fallidos == null ? List.of() : List.copyOf(fallidos);
    }

    public record Fallido(Long legajoId, String motivo) {
    }
}
