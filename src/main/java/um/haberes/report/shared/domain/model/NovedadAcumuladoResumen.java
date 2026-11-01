package um.haberes.report.shared.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Acumulado de novedades de un legajo para un código y período. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovedadAcumuladoResumen {

    private Long legajoId;
    private Integer codigoId;
    private Integer anho;
    private Integer mes;
    private BigDecimal importe;
    private Integer cantidad;
}
