package um.haberes.report.shared.infrastructure.client.haberes.core.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.shared.domain.model.CursoResumen;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CursoDto;

@Component
@RequiredArgsConstructor
public class CursoMapper {

    private final FacultadMapper facultadMapper;
    private final GeograficaMapper geograficaMapper;

    public CursoResumen toDomain(CursoDto dto) {
        if (dto == null) {
            return null;
        }
        return CursoResumen.builder()
                .cursoId(dto.getCursoId())
                .nombre(dto.getNombre())
                .facultadId(dto.getFacultadId())
                .geograficaId(dto.getGeograficaId())
                .anual(dto.getAnual())
                .semestre1(dto.getSemestre1())
                .semestre2(dto.getSemestre2())
                .facultad(facultadMapper.toDomain(dto.getFacultad()))
                .geografica(geograficaMapper.toDomain(dto.getGeografica()))
                .build();
    }
}
