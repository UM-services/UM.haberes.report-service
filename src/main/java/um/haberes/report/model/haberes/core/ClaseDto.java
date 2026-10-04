package um.haberes.report.model.haberes.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaseDto {

    private Integer claseId;

    private String nombre = "";

    private BigDecimal valorHora = BigDecimal.ZERO;

}
