package um.haberes.report.shared.infrastructure.client.haberes.core.dto;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcluidoDto {

    private Long excluidoId;

    private Long legajoId;

    private int anho = 0;

    private int mes = 0;

    private OffsetDateTime fecha;

    private String observaciones = "";
}
