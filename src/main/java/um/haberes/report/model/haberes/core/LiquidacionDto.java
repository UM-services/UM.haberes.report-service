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
public class LiquidacionDto {

    private Long liquidacionId;

    private Long legajoId;

    private int anho = 0;

    private int mes = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaLiquidacion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaAcreditacion;

    private Integer dependenciaId;

    private String salida;

    private BigDecimal totalRemunerativo = BigDecimal.ZERO;

    private BigDecimal totalNoRemunerativo = BigDecimal.ZERO;

    private BigDecimal totalDeduccion = BigDecimal.ZERO;

    private BigDecimal totalNeto = BigDecimal.ZERO;

    private byte bloqueado = 0;

    private int estado = 0;

    private String liquida = "";

    private PersonaDto persona;

    private DependenciaDto dependencia;

}
