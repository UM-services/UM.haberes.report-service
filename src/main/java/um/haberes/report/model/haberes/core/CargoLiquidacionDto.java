package um.haberes.report.model.haberes.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CargoLiquidacionDto {

    private Long cargoLiquidacionId;

    private Long legajoId;

    private Integer anho;

    private Integer mes;

    private Integer dependenciaId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaDesde;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaHasta;

    private Integer categoriaId;

    private String categoriaNombre = "";

    private BigDecimal categoriaBasico = BigDecimal.ZERO;

    private BigDecimal horasJornada = BigDecimal.ZERO;

    private int jornada = 0;

    private int presentismo = 0;

    private String situacion;

    private PersonaDto persona;

    private DependenciaDto dependencia;

    private CategoriaDto categoria;

}
