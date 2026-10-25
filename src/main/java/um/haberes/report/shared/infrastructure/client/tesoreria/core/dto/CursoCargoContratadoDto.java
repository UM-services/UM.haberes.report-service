package um.haberes.report.shared.infrastructure.client.tesoreria.core.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ContratadoPersonaDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoCargoContratadoDto {

    private Long cursoCargoContratadoId;

    private Long cursoId;

    private int anho = 0;

    private int mes = 0;

    private Long contratadoId;

    private Long contratoId;

    private java.math.BigDecimal personaId;

    private Integer documentoId;

    private Integer cargoTipoId;

    private BigDecimal horasSemanales = BigDecimal.ZERO;

    private BigDecimal horasTotales = BigDecimal.ZERO;

    private Integer designacionTipoId;

    private Integer categoriaId;

    private Long cursoCargoNovedadId;

    private byte acreditado = 0;

    private ContratadoPersonaDto contratadoPersona;

}
