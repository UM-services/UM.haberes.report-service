package um.haberes.report.model.haberes.core;

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
