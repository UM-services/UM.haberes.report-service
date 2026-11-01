package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CursoCargoNovedadResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoCargoNovedadDto;

@Component
@RequiredArgsConstructor
public class CursoCargoNovedadMapper {

    private final CursoMapper cursoMapper;
    private final CargoTipoMapper cargoTipoMapper;
    private final PersonaMapper personaMapper;

    public CursoCargoNovedadResumen toDomain(CursoCargoNovedadDto dto) {
        if (dto == null) {
            return null;
        }
        return CursoCargoNovedadResumen.builder()
                .legajoId(dto.getLegajoId())
                .cursoId(dto.getCursoId())
                .anho(dto.getAnho())
                .mes(dto.getMes())
                .cargoTipoId(dto.getCargoTipoId())
                .horasSemanales(dto.getHorasSemanales())
                .desarraigo(dto.getDesarraigo())
                .alta(dto.getAlta())
                .baja(dto.getBaja())
                .cambio(dto.getCambio())
                .solicitud(dto.getSolicitud())
                .autorizado(dto.getAutorizado())
                .rechazado(dto.getRechazado())
                .respuesta(dto.getRespuesta())
                .curso(cursoMapper.toDomain(dto.getCurso()))
                .cargoTipo(cargoTipoMapper.toDomain(dto.getCargoTipo()))
                .persona(personaMapper.toDomain(dto.getPersona()))
                .build();
    }
}
