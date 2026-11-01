package um.haberes.report.shared.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CargoLiquidacionResumen {

    private Long legajoId;
    private Integer anho;
    private Integer mes;
    private Integer dependenciaId;
    private Integer categoriaId;
    private String categoriaNombre;
    private BigDecimal categoriaBasico;
    private BigDecimal horasJornada;
    private int jornada;
    private int presentismo;
    private String situacion;
    private OffsetDateTime fechaDesde;
    private OffsetDateTime fechaHasta;
    private DependenciaResumen dependencia;
    private CategoriaResumen categoria;
}
