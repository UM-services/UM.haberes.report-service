package um.haberes.report.shared.infrastructure.client.haberes.core.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NovedadAcumuladoDto {

    private String unified;

    private Long legajoId;

    private Integer anho;

    private Integer mes;

    private Integer codigoId;

    private BigDecimal importe;

    private Integer cantidad;
}
