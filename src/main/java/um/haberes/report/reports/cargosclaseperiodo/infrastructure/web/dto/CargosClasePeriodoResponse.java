package um.haberes.report.reports.cargosclaseperiodo.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record CargosClasePeriodoResponse(Integer anho, Integer mes, String facultad,
                                         List<Sede> sedes) {

    public CargosClasePeriodoResponse {
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
