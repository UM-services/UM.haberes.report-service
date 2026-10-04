package um.haberes.report.model.haberes.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoCargoDto {

    private Long cursoCargoId;

    private Long cursoId;

    private int anho = 0;

    private int mes = 0;

    private Integer cargoTipoId;

    private Long legajoId;

    private BigDecimal horasSemanales = BigDecimal.ZERO;

    private BigDecimal horasTotales = BigDecimal.ZERO;

    private Integer designacionTipoId;

    private Integer categoriaId;

    private byte desarraigo = 0;

    private Long cursoCargoNovedadId;

    private CursoDto curso;

    private CargoTipoDto cargoTipo;

    private PersonaDto persona;

    private DesignacionTipoDto designacionTipo;

    private CategoriaDto categoria;

}
