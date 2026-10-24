package um.haberes.report.reports.anotador.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.anotador.domain.model.AnotadorRegistro;
import um.haberes.report.reports.anotador.domain.ports.out.AnotadorCatalogPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.AnotadorClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.AnotadorDto;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FeignAnotadorCatalogAdapter implements AnotadorCatalogPort {

    private final AnotadorClient anotadorClient;
    private final AnotadorMapper mapper;

    @Override
    public List<AnotadorRegistro> findPendientes(Integer anho, Integer mes) {
        return map(anotadorClient.findPendientes(anho, mes));
    }

    @Override
    public List<AnotadorRegistro> findPendientesByFacultad(Integer facultadId, Integer anho,
                                                           Integer mes) {
        return map(anotadorClient.findPendientesByFacultad(facultadId, anho, mes));
    }

    @Override
    public List<AnotadorRegistro> findRevisados(Integer anho, Integer mes) {
        return map(anotadorClient.findRevisados(anho, mes));
    }

    @Override
    public List<AnotadorRegistro> findAutorizadosByFacultad(Integer facultadId, Integer anho,
                                                            Integer mes) {
        return map(anotadorClient.findAutorizadosByFacultad(facultadId, anho, mes));
    }

    @Override
    public List<AnotadorRegistro> findRechazadosByFacultad(Integer facultadId, Integer anho,
                                                           Integer mes) {
        return map(anotadorClient.findRechazadosByFacultad(facultadId, anho, mes));
    }

    private List<AnotadorRegistro> map(List<AnotadorDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(mapper::toDomain).filter(Objects::nonNull).toList();
    }
}
