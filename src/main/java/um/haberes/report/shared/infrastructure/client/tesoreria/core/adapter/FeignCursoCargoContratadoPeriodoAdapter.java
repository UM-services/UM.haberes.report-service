package um.haberes.report.shared.infrastructure.client.tesoreria.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CursoCargoContratadoResumen;
import um.haberes.report.shared.domain.ports.out.CursoCargoContratadoCatalogPort;
import um.haberes.report.shared.infrastructure.client.tesoreria.core.client.CursoCargoContratadoClient;
import um.haberes.report.shared.infrastructure.client.tesoreria.core.dto.CursoCargoContratadoDto;
import um.haberes.report.shared.infrastructure.client.tesoreria.core.mapper.CursoCargoContratadoMapper;

import java.util.List;
import java.util.Objects;

/**
 * Adaptador del período para los cursos de contratados. El slice {@code docentessede} tiene su
 * propio adaptador por curso; este cubre la consulta global de la planilla de contratados.
 */
@Component
@RequiredArgsConstructor
public class FeignCursoCargoContratadoPeriodoAdapter implements CursoCargoContratadoCatalogPort {

    private final CursoCargoContratadoClient cursoCargoContratadoClient;
    private final CursoCargoContratadoMapper cursoCargoContratadoMapper;

    @Override
    public List<CursoCargoContratadoResumen> findAllByPeriodo(Integer anho, Integer mes) {
        List<CursoCargoContratadoDto> dtos = cursoCargoContratadoClient.findAllByPeriodo(anho, mes);
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(cursoCargoContratadoMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
