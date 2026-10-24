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
public class LiquidacionResumen {

    private Long legajoId;
    private int anho;
    private int mes;
    private Integer dependenciaId;
    private BigDecimal totalRemunerativo;
    private BigDecimal totalNoRemunerativo;
    private BigDecimal totalDeduccion;
    private BigDecimal totalNeto;
}
