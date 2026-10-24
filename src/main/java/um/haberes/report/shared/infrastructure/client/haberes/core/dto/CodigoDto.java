package um.haberes.report.shared.infrastructure.client.haberes.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodigoDto {

    private Integer codigoId;

    private String nombre = "";

    private byte docente = 0;

    private byte noDocente = 0;

    private byte transferible = 0;

    private byte incluidoEtec = 0;

    private Long afipConceptoSueldoIdPrimerSemestre;

    private Long afipConceptoSueldoIdSegundoSemestre;

    private AfipConceptoSueldoDto afipConceptoSueldoPrimerSemestre;

    private AfipConceptoSueldoDto afipConceptoSueldoSegundoSemestre;

}
