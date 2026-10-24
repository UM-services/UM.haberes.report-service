package um.haberes.report.shared.infrastructure.client.haberes.core.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.DocenteDesignacionResumen;
import um.haberes.report.shared.domain.ports.out.DocenteDesignacionCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.DocenteDesignacionClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.DocenteDesignacionDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.mapper.DocenteDesignacionMapper;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignDocenteDesignacionCatalogAdapter implements DocenteDesignacionCatalogPort {

    private final DocenteDesignacionClient docenteDesignacionClient;
    private final DocenteDesignacionMapper docenteDesignacionMapper;

    @Override
    public List<DocenteDesignacionResumen> findAllByPeriodo(Integer anho, Integer mes) {
        List<DocenteDesignacionDto> dtos = docenteDesignacionClient.findAllByPeriodo(anho, mes);
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(docenteDesignacionMapper::toDomain).filter(Objects::nonNull).toList();
    }
}
