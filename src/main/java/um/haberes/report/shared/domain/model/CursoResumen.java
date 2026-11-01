package um.haberes.report.shared.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoResumen {

    private Long cursoId;
    private String nombre;
    private Integer facultadId;
    private Integer geograficaId;
    private byte anual;
    private byte semestre1;
    private byte semestre2;
    private FacultadResumen facultad;
    private GeograficaResumen geografica;
}
