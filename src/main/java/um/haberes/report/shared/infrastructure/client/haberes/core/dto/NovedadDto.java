package um.haberes.report.shared.infrastructure.client.haberes.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NovedadDto {

    private Long novedadId;

    private Long legajoId;

    private Integer anho;

    private Integer mes;

    private Integer codigoId;

    private Integer dependenciaId;

    private BigDecimal importe = BigDecimal.ZERO;

    private String value = "";

    private String observaciones = "";

    private Byte importado = 0;

    private Long novedadUploadId;

}
