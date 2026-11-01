package um.haberes.report.reports.totalesgenerales.infrastructure.client;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.totalesgenerales.domain.model.CodigoGrupo;
import um.haberes.report.reports.totalesgenerales.domain.model.TotalMensualCodigo;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoGrupoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.TotalMensualDto;

@Component
public class TotalesGeneralesMapper {

    public TotalMensualCodigo toTotalMensual(TotalMensualDto dto) {
        if (dto == null) {
            return null;
        }
        return new TotalMensualCodigo(dto.getCodigoId(), dto.getTotal());
    }

    public CodigoGrupo toCodigoGrupo(CodigoGrupoDto dto) {
        if (dto == null) {
            return null;
        }
        Integer codigoId = dto.getCodigoId() != null
                ? dto.getCodigoId()
                : (dto.getCodigo() == null ? null : dto.getCodigo().getCodigoId());
        return new CodigoGrupo(codigoId, dto.getRemunerativo() == 1,
                dto.getNoRemunerativo() == 1, dto.getDeduccion() == 1, dto.getTotal() == 1);
    }
}
