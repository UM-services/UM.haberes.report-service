package um.haberes.report.shared.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Adicional liquidado de un legajo en el período (fuente de la planilla de básicos). */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionAdicionalResumen {

    private Long legajoId;
    private Integer anho;
    private Integer mes;
    private Integer dependenciaId;
    private String dependenciaNombre;
    private BigDecimal adicional;
}
