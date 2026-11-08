package um.haberes.report.reports.bono.domain.ports.in;

import um.haberes.report.reports.bono.domain.model.BonosDependenciaLoteResultado;

/**
 * Genera el PDF consolidado de toda la dependencia en una sola pasada, tolerando legajos
 * fallidos. Reemplaza la generación uno por uno que colgaba la request del navegador.
 */
public interface GenerateBonosDependenciaLoteUseCase {

    BonosDependenciaLoteResultado generar(Integer anho, Integer mes, Integer dependenciaId,
                                          String salida, String jobId, BonoLoteProgreso progreso);
}
