package um.haberes.report.shared.infrastructure.client.haberes.core.dto;

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
