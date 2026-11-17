package um.haberes.report.shared.domain.model;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Legajo excluido de la acreditación de un período. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcluidoResumen {

    private Long legajoId;
    private Integer anho;
    private Integer mes;
    private OffsetDateTime fecha;
    private String observaciones;
}
