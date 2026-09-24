package um.haberes.report.model.haberes.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CargoTipoDto {

    private Integer cargoTipoId;

    private byte aCargo = 0;

    private String nombre = "";

    private int precedencia = 0;

}
