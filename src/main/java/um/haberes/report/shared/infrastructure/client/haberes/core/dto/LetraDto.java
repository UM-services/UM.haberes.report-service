package um.haberes.report.shared.infrastructure.client.haberes.core.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LetraDto {

    private Long letraId;

    private Long legajoId;

    private int anho = 0;

    private int mes = 0;

    private BigDecimal neto = BigDecimal.ZERO;

    private String cadena = "";

}
