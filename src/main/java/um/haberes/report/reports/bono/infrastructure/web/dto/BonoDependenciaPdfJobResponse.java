package um.haberes.report.reports.bono.infrastructure.web.dto;

import java.util.List;

/** Estado de la generación asincrónica del PDF consolidado. */
public record BonoDependenciaPdfJobResponse(String jobId, String estado, int procesados, int total,
                                            String mensaje,
                                            List<BonoDependenciaPdfJobResponse.Fallido> fallidos) {

    public record Fallido(String legajoId, String motivo) {
    }
}
