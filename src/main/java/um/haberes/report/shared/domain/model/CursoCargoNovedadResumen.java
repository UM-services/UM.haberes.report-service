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
public class CursoCargoNovedadResumen {

    private Long legajoId;
    private Long cursoId;
    private int anho;
    private int mes;
    private Integer cargoTipoId;
    private BigDecimal horasSemanales;
    private byte desarraigo;
    private byte alta;
    private byte baja;
    private byte cambio;
    private String solicitud;
    private byte autorizado;
    private byte rechazado;
    private String respuesta;
    private CursoResumen curso;
    private CargoTipoResumen cargoTipo;
    private PersonaResumen persona;
}
