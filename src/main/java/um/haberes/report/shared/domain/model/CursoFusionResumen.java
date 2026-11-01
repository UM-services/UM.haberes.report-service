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
public class CursoFusionResumen {

    private Long cursoFusionId;
    private Long legajoId;
    private int anho;
    private int mes;
    private Integer facultadId;
    private Integer geograficaId;
    private Integer cargoTipoId;
    private Integer designacionTipoId;
    private Integer categoriaId;
    private byte anual;
}
