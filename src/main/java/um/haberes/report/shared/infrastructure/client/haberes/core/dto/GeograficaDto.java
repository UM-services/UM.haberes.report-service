package um.haberes.report.shared.infrastructure.client.haberes.core.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeograficaDto {

    private Integer geograficaId;

    private String nombre = "";

    private String reducido = "";

    private BigDecimal desarraigo = BigDecimal.ZERO;

    private Integer geograficaIdReemplazo;

}
