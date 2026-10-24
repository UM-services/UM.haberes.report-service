package um.haberes.report.shared.infrastructure.client.haberes.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactoDto {

    private Long legajoId;

    private String fijo = "";

    private String movil = "";

    private String mailPersonal = "";

    private String mailInstitucional = "";

}
