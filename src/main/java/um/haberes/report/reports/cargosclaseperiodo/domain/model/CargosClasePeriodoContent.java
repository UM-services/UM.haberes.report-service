package um.haberes.report.reports.cargosclaseperiodo.domain.model;

import java.util.List;

/** Contenido ya calculado del reporte: una seccion por sede con sus filas. */
public record CargosClasePeriodoContent(Integer anho, Integer mes, String facultad,
                                        List<Sede> sedes) {

    public CargosClasePeriodoContent {
        sedes = List.copyOf(sedes);
    }

    public record Sede(String nombre, List<Fila> filas) {

        public Sede {
            filas = List.copyOf(filas);
        }
    }

    public record Fila(Long legajoId, String apellidoNombre, String cargo, String descripcion,
                       Long periodoDesde, Long periodoHasta, Integer horas, String clase) {
    }
}
