package um.haberes.report.shared.infrastructure.client.haberes.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CargoClaseDto {

    private Long cargoClaseId;

    private String nombre = "";

    private Integer claseId;

    private ClaseDto clase;

}
