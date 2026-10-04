package um.haberes.report.model.tesoreria.core;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDto {

    private Long uniqueId;

    private BigDecimal personaId;

    private Integer documentoId;

    private String apellido;

    private String nombre;

    private String sexo;

    private byte primero = 0;

    private String cuit = "";

    private String cbu = "";

    private String password;

    public String getApellidoNombre() {
        return this.apellido + ", " + this.nombre;
    }

}
