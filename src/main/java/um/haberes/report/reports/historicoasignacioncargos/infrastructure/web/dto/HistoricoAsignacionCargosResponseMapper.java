package um.haberes.report.reports.historicoasignacioncargos.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.historicoasignacioncargos.domain.model.HistoricoAsignacionCargosContent;

@Component
public class HistoricoAsignacionCargosResponseMapper {

    static final String AVISO =
            "El reporte no tiene cuerpo implementado; se descarga únicamente el encabezado.";

    public HistoricoAsignacionCargosResponse toResponse(HistoricoAsignacionCargosContent content) {
        return new HistoricoAsignacionCargosResponse(
                content == null || content.categoriaId() == null ? "" : content.categoriaId().toString(),
                AVISO);
    }
}
