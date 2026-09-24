package um.haberes.report.model.haberes.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoFusionDto {

    private Long cursoFusionId;

    private Long legajoId;

    private int anho = 0;

    private int mes = 0;

    private Integer facultadId;

    private Integer geograficaId;

    private Integer cargoTipoId;

    private Integer designacionTipoId;

    private byte anual = 0;

    private Integer categoriaId;

}
