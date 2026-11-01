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
public class CursoCargoResumen {

    private Long legajoId;
    private Long cursoId;
    private Integer cargoTipoId;
    private Integer categoriaId;
    private Integer designacionTipoId;
    private BigDecimal horasSemanales;
    private byte desarraigo;
    private CursoResumen curso;
    private CargoTipoResumen cargoTipo;
    private CategoriaResumen categoria;
    private DesignacionTipoResumen designacionTipo;
    private PersonaResumen persona;
}
