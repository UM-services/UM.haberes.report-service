package um.haberes.report.reports.bono.domain.model;

import java.util.List;

/** Contenido ya calculado del detalle de cargos. */
public record DetalleCargosContent(Long legajoId, String apellidoNombre, String antiguedad,
                                   String facultad, List<BonoContent.Curso> cursos,
                                   List<BonoContent.Cargo> docentes,
                                   List<BonoContent.Cargo> noDocentes,
                                   List<BonoContent.Cargo> clases) {

    public DetalleCargosContent {
        cursos = List.copyOf(cursos);
        docentes = List.copyOf(docentes);
        noDocentes = List.copyOf(noDocentes);
        clases = List.copyOf(clases);
    }
}
