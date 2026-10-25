package um.haberes.report.reports.cargosperiodo.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record CargosPeriodoResponse(Integer anho, Integer mes, List<Fila> filas) {

    public CargosPeriodoResponse {
        filas = List.copyOf(filas);
    }

    public record Fila(String legajoId, String apellidoNombre, String periodo, String dependenciaId,
                       String dependenciaNombre, String categoriaId, String categoriaNombre) {
    }
}
