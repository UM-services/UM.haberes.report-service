package um.haberes.report.model.haberes.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CargoClasePeriodoDto {

    private Long cargoClasePeriodoId;

    private Long legajoId;

    private Long cargoClaseId;

    private Integer dependenciaId;

    private Integer facultadId;

    private Integer geograficaId;

    private Long periodoDesde;

    private Long periodoHasta;

    private int horas = 0;

    private BigDecimal valorHora = BigDecimal.ZERO;

    private String descripcion;

    private PersonaDto persona;

    private CargoClaseDto cargoClase;

    private DependenciaDto dependencia;

    private FacultadDto facultad;

    private GeograficaDto geografica;

}
