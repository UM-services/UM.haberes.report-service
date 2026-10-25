package um.haberes.report.shared.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Curso-cargo de un contrato en el período (tesoreria-core). */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoCargoContratadoResumen {

    private Long cursoCargoContratadoId;
    private Long cursoId;
    private Integer anho;
    private Integer mes;
    private Long contratoId;
    private BigDecimal personaId;
    private Integer documentoId;
    private Integer cargoTipoId;
    private BigDecimal horasSemanales;
}
