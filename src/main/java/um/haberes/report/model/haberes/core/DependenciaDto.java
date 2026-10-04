package um.haberes.report.model.haberes.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DependenciaDto {

    private Integer dependenciaId;

    private String nombre = "";

    private String acronimo = "";

    private Integer facultadId;

    private Integer geograficaId;

    private FacultadDto facultad;

    private GeograficaDto geografica;

}
