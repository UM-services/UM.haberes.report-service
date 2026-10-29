package um.haberes.report.reports.designacionesdetalle.domain.model;

import java.util.List;

/** Contenido ya calculado del detalle de designaciones de un legajo. */
public record DesignacionesDetalleContent(Long legajoId, String persona, Integer anho, Integer mes,
                                          Periodo periodoAnterior,
                                          List<LiquidaFila> liquidadas, String totalLiquidadas,
                                          List<CursoFila> cursos, String totalCursos,
                                          List<FusionFila> fusiones, String totalFusion,
                                          String diferencia) {

    public DesignacionesDetalleContent {
        liquidadas = List.copyOf(liquidadas);
        cursos = List.copyOf(cursos);
        fusiones = List.copyOf(fusiones);
    }

    public record Periodo(int anho, int mes) {
    }

    public record LiquidaFila(String facultad, String sede, String designacion, String importe) {
    }

    public record CursoFila(String facultad, String sede, String curso, String horas, String cargo,
                            String designacion, String importe, String desarraigo, String anual,
                            String semestre1, String semestre2) {
    }

    public record FusionFila(String facultad, String sede, String cargo, String designacion,
                             String categoria, String importe) {
    }
}
