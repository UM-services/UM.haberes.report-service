package um.haberes.report.reports.totalesgenerales.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.totalesgenerales.domain.model.CodigoGrupo;
import um.haberes.report.reports.totalesgenerales.domain.ports.out.CodigoGrupoCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.CodigoGrupoClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CodigoGrupoDto;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignCodigoGrupoCatalogAdapter implements CodigoGrupoCatalogPort {

    private final CodigoGrupoClient codigoGrupoClient;
    private final TotalesGeneralesMapper mapper;

    @Override
    public List<CodigoGrupo> findAll() {
        List<CodigoGrupoDto> dtos = codigoGrupoClient.findAll();
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(mapper::toCodigoGrupo).filter(Objects::nonNull).toList();
    }
}
