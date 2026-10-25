package um.haberes.report.reports.totalesgenerales.domain.ports.in;

import um.haberes.report.reports.totalesgenerales.domain.model.TotalesGeneralesContent;

/**
 * Calcula el contenido del reporte una sola vez. Es la fuente única que consumen la tabla
 * ({@code /ui/totalesGenerales/data}), el PDF y el XLSX, para que los tres muestren lo mismo.
 */
public interface BuildTotalesGeneralesContentUseCase {

    TotalesGeneralesContent build(Integer anho, Integer mes);
}
