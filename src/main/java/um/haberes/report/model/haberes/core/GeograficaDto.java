package um.haberes.report.model.haberes.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.haberes.report.util.Jsonifyable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeograficaDto implements Jsonifyable {

    private Integer geograficaId;

    private String nombre = "";

    private String reducido = "";

    private BigDecimal desarraigo = BigDecimal.ZERO;

    private Integer geograficaIdReemplazo;

}
