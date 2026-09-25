package um.haberes.report.model.haberes.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CargoClaseDetalleDto {

    private Long cargoClaseDetalleId;

    private Long legajoId;

    private Integer anho = 0;

    private Integer mes = 0;

    private Long cargoClaseId;

    private Integer dependenciaId;

    private Integer facultadId;

    private Integer geograficaId;

    private int horas = 0;

    private BigDecimal valorHora = BigDecimal.ZERO;

    private Long cargoClasePeriodoId;

    private byte liquidado = 0;

    private PersonaDto persona;

    private CargoClaseDto cargoClase;

    private DependenciaDto dependencia;

    private FacultadDto facultad;

    private GeograficaDto geografica;

    private CargoClasePeriodoDto cargoClasePeriodo;

}
