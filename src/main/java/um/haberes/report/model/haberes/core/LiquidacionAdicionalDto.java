package um.haberes.report.model.haberes.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionAdicionalDto {

    private Long liquidacionAdicionalId;

    private Long legajoId;

    private Integer anho;

    private Integer mes;

    private Integer dependenciaId;

    private BigDecimal adicional = BigDecimal.ZERO;

    private PersonaDto persona;

    private DependenciaDto dependencia;

}
