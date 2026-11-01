package um.haberes.report.reports.docentessede.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.docentessede.domain.model.CursoCargoContratado;
import um.haberes.report.reports.docentessede.domain.ports.out.CursoCargoContratadoCatalogPort;
import um.haberes.report.shared.infrastructure.client.tesoreria.core.client.CursoCargoContratadoClient;
import um.haberes.report.shared.infrastructure.client.tesoreria.core.dto.CursoCargoContratadoDto;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignCursoCargoContratadoCatalogAdapter implements CursoCargoContratadoCatalogPort {

    private final CursoCargoContratadoClient cursoCargoContratadoClient;
    private final DocentesSedeMapper mapper;

    @Override
    public List<CursoCargoContratado> findAllByCurso(Long cursoId, Integer anho, Integer mes) {
        List<CursoCargoContratadoDto> dtos =
                cursoCargoContratadoClient.findAllByCurso(cursoId, anho, mes);
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(mapper::toDomain).filter(Objects::nonNull).toList();
    }
}
