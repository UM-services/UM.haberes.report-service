package um.haberes.report.reports.cargosclase.domain.model;

import um.haberes.report.shared.domain.model.ClaseResumen;

import java.util.List;

/** Contenido ya calculado del reporte: una seccion por clase con sus cargos ordenados. */
public record CargosClaseContent(List<ClaseConCargos> clases) {

    public CargosClaseContent {
        clases = List.copyOf(clases);
    }

    public record ClaseConCargos(ClaseResumen clase, List<CargoClase> cargos) {

        public ClaseConCargos {
            cargos = List.copyOf(cargos);
        }
    }
}
