package um.haberes.report.model.haberes.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.haberes.report.util.Jsonifyable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodigoDto implements Jsonifyable {

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
