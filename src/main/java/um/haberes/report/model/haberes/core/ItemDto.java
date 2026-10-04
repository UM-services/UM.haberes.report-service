package um.haberes.report.model.haberes.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Long itemId;

    private Long legajoId;

    private int anho = 0;

    private int mes = 0;

    private Integer codigoId;

    private String codigoNombre = "";

    private BigDecimal importe = BigDecimal.ZERO;

    private PersonaDto persona;

    private CodigoDto codigo;

}
