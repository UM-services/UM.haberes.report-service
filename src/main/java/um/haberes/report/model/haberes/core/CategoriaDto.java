package um.haberes.report.model.haberes.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDto {

    private Integer categoriaId;

    private String nombre = "";

    private BigDecimal basico = BigDecimal.ZERO;

    private byte docente = 0;

    private byte noDocente = 0;

    private byte liquidaPorHora = 0;

}
