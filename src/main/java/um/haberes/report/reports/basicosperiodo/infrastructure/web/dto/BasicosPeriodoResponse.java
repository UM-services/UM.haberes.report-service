package um.haberes.report.reports.basicosperiodo.infrastructure.web.dto;

import java.util.List;

/** Contrato HTTP del reporte para la tabla del frontend. */
public record BasicosPeriodoResponse(Integer anho, Integer mes, List<Fila> filas) {

    public BasicosPeriodoResponse {
        filas = List.copyOf(filas);
    }

    public record Fila(String legajoId, String apellidoNombre, String dependencia, String claseCodigo,
                       String categoriaCargo, String basico, String horasJornada, String jornada) {
    }
}
