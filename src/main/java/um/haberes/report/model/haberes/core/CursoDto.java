package um.haberes.report.model.haberes.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoDto {

    private Long cursoId;

    private String nombre = "";

    private Integer facultadId;

    private Integer geograficaId;

    private byte anual = 0;

    private byte semestre1 = 0;

    private byte semestre2 = 0;

    private Integer nivelId;

    private byte adicionalCargaHoraria = 0;

    private FacultadDto facultad;

    private GeograficaDto geografica;

    private NivelDto nivel;

}
