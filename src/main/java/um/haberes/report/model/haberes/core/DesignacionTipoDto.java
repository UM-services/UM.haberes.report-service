package um.haberes.report.model.haberes.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesignacionTipoDto {

    private int designacionTipoId = 0;

    private String nombre = "";

    private BigDecimal horasSemanales = BigDecimal.ZERO;

    private BigDecimal horasTotales = BigDecimal.ZERO;

    private int simples = 0;

}
