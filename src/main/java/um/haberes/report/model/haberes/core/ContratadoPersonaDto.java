package um.haberes.report.model.haberes.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContratadoPersonaDto {

    private Long contratadoId;

    private BigDecimal personaId;

    private Integer documentoId;

    private String apellido;

    private String nombre;

    private String cuit;

}
