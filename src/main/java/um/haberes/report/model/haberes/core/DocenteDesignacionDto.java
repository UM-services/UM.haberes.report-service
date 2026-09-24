package um.haberes.report.model.haberes.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocenteDesignacionDto {

    private Long legajoId;

    private Integer anho;

    private Integer mes;

    private Integer facultadId;

    private Integer geograficaId;

    private String espacio;

    private BigDecimal horasSemanales;

    private String cargo;

    private String designacion;

    private BigDecimal horasDesignacion;

    private Byte anual;

    private Byte semestre1;

    private Byte semestre2;

}
