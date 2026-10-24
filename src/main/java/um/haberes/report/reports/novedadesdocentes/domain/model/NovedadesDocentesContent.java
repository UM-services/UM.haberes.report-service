package um.haberes.report.reports.novedadesdocentes.domain.model;

import java.util.List;

/** Contenido ya calculado: una seccion por sede con sus bloques ALTA/CAMBIO/BAJA. */
public record NovedadesDocentesContent(Integer anho, Integer mes, String facultad,
                                       List<Sede> sedes) {

    public NovedadesDocentesContent {
        sedes = List.copyOf(sedes);
    }

    public record Sede(String nombre, List<Seccion> secciones) {

        public Sede {
            secciones = List.copyOf(secciones);
        }
    }

    public record Seccion(String titulo, List<Novedad> novedades) {

        public Seccion {
            novedades = List.copyOf(novedades);
        }
    }

    public record Novedad(String curso, String cargo, String docente, String horas,
                          String desarraigo, String autorizado, String rechazado,
                          String solicitud, String respuesta) {
    }
}
