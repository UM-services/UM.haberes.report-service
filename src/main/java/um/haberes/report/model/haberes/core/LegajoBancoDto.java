package um.haberes.report.model.haberes.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegajoBancoDto {

    private Long legajoBancoId;

    private Long legajoId;

    private int anho = 0;

    private int mes = 0;

    private String cbu = "";

    private BigDecimal fijo = BigDecimal.ZERO;

    private BigDecimal porcentaje = BigDecimal.ZERO;

    private byte resto = 0;

    private BigDecimal acreditado = BigDecimal.ZERO;

    private PersonaDto persona;

    private LiquidacionDto liquidacion;

}
