package um.haberes.report.model.haberes.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AntiguedadDto {

    private Long antiguedadId;

    private Long legajoId;

    private int anho = 0;

    private int mes = 0;

    private int mesesDocentes = 0;

    private int mesesAdministrativos = 0;

    private PersonaDto persona;

}
