package um.haberes.report.shared.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Persona contratada (proveedor de servicios) que puede tener cursos en el período. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContratadoPersonaResumen {

    private BigDecimal personaId;
    private Integer documentoId;
    private String apellido;
    private String nombre;
    private String cuit;

    public String apellidoNombre() {
        return (apellido == null ? "" : apellido) + ", " + (nombre == null ? "" : nombre);
    }
}
