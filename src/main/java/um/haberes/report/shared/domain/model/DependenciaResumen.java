package um.haberes.report.shared.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DependenciaResumen {

    private Integer dependenciaId;
    private String nombre;
    private String acronimo;
    private Integer facultadId;
    private Integer geograficaId;
}
