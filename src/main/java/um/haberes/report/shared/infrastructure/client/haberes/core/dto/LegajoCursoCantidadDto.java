package um.haberes.report.shared.infrastructure.client.haberes.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegajoCursoCantidadDto {

    private String uniqueId;

    private Integer anho;

    private Integer mes;

    private Long legajoId;

    private Integer facultadId;

    private Integer geograficaId;

    private Integer anuales;

    private Integer semestre1;

    private Integer semestre2;
}
