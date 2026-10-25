package um.haberes.report.reports.historicoasignacioncargos.infrastructure.web.dto;

/**
 * Contrato HTTP del reporte. No tiene filas: el reporte original nunca implementó el cuerpo,
 * y la UI muestra el aviso en lugar de una tabla vacía.
 */
public record HistoricoAsignacionCargosResponse(String categoriaId, String aviso) {
}
