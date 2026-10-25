package um.haberes.report.shared.infrastructure.client.haberes.core.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Respuesta de {@code GET /api/haberes/core/formularioImprimir/{anho}/{mes}/{dep}/{filtro}}. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormularioImprimirDto {

    private List<LegajoControlDto> legajoControls;

    private List<ContactoDto> contactos;
}
