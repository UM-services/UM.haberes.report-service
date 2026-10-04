package um.haberes.report.model.haberes.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AfipConceptoSueldoDto {

    private Long afipConceptoSueldoId;

    private String descripcion = "";

    private byte asignado = 0;

}
