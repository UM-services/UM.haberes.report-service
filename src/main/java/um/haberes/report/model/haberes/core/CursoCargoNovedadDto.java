package um.haberes.report.model.haberes.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoCargoNovedadDto {

    private Long cursoCargoNovedadId;

    private Long cursoId;

    private int anho = 0;

    private int mes = 0;

    private Integer cargoTipoId;

    private Long legajoId;

    private BigDecimal horasSemanales = BigDecimal.ZERO;

    private BigDecimal horasTotales = BigDecimal.ZERO;

    private byte desarraigo = 0;

    private byte alta = 0;

    private byte baja = 0;

    private byte cambio = 0;

    private String solicitud;

    private byte autorizado = 0;

    private byte rechazado = 0;

    private String respuesta;

    private byte transferido = 0;

    private CursoDto curso;

    private CargoTipoDto cargoTipo;

    private PersonaDto persona;

}
