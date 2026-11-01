package um.haberes.report.shared.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocenteDesignacionResumen {

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
