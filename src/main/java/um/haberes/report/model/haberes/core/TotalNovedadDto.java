package um.haberes.report.model.haberes.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalNovedadDto {

    private String uniqueId;

    private Integer anho;

    private Integer mes;

    private Integer codigoId;

    private BigDecimal total = BigDecimal.ZERO;

}
