package um.haberes.report.reports.docentessede.domain.model;

import java.math.BigDecimal;

/** Cargo contratado de un curso, resuelto contra tesoreria-core. */
public record CursoCargoContratado(Long cursoId, Integer cargoTipoId,
                                   BigDecimal personaId, Integer documentoId,
                                   BigDecimal horasSemanales) {
}
