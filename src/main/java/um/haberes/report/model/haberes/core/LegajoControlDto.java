package um.haberes.report.model.haberes.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegajoControlDto {

    private Long legajoControlId;

    private Long legajoId;

    private int anho = 0;

    private int mes = 0;

    private byte liquidado = 0;

    private byte fusionado = 0;

    private byte bonoEnviado = 0;

    private PersonaDto persona;

}
