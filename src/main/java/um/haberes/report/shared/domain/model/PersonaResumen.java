package um.haberes.report.shared.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Datos de persona que consumen dos o mas reportes. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaResumen {

    private Long legajoId;
    private BigDecimal documento;
    private String apellido;
    private String nombre;
    private String cuil;
    private Integer dependenciaId;
    private byte directivoEtec;
    private OffsetDateTime altaAdministrativa;

    public String apellidoNombre() {
        return apellido + ", " + nombre;
    }
}
