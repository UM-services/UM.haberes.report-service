package um.haberes.report.reports.cursocargonovedad.domain.model;

import java.util.List;

/** Contenido ya calculado del reporte de novedades de cursos. */
public record CursocargoNovedadContent(Integer anho, Integer mes, boolean soloPendientes,
                                       List<Fila> filas) {

    public CursocargoNovedadContent {
        filas = List.copyOf(filas);
    }

    public record Fila(String facultad, String sede, String legajo, String docente, String curso,
                       String cargo, String horas, String desarraigo, String tipo, String estado,
                       String solicitud) {
    }
}
