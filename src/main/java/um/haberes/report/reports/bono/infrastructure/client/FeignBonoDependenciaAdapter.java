package um.haberes.report.reports.bono.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.bono.domain.model.BonosDependenciaContent;
import um.haberes.report.reports.bono.domain.ports.out.BonoDependenciaDataPort;
import um.haberes.report.shared.infrastructure.client.haberes.core.client.FormularioImprimirClient;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ContactoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FormularioImprimirDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.LegajoControlDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Una sola llamada a {@code formularioImprimir}: el core ya devuelve los legajos con su persona
 * y los contactos con el mail institucional.
 */
@Component
@RequiredArgsConstructor
public class FeignBonoDependenciaAdapter implements BonoDependenciaDataPort {

    private final FormularioImprimirClient formularioImprimirClient;

    @Override
    public List<BonosDependenciaContent.Fila> legajos(Integer anho, Integer mes,
                                                      Integer dependenciaId, String salida) {
        FormularioImprimirDto data = formularioImprimirClient.findData(anho, mes, dependenciaId,
                salida);
        if (data == null || data.getLegajoControls() == null) {
            return List.of();
        }
        Map<Long, ContactoDto> contactos = (data.getContactos() == null ? List.<ContactoDto>of()
                : data.getContactos()).stream()
                .filter(Objects::nonNull)
                .filter(contacto -> contacto.getLegajoId() != null)
                .collect(Collectors.toMap(ContactoDto::getLegajoId, Function.identity(), (a, b) -> a));
        return data.getLegajoControls().stream()
                .filter(Objects::nonNull)
                .filter(legajoControl -> legajoControl.getLegajoId() != null)
                .map(legajoControl -> fila(legajoControl, contactos.get(legajoControl.getLegajoId())))
                .toList();
    }

    private BonosDependenciaContent.Fila fila(LegajoControlDto legajoControl, ContactoDto contacto) {
        PersonaDto persona = legajoControl.getPersona();
        String apellidoNombre = persona == null ? ""
                : (persona.getApellido() == null ? "" : persona.getApellido()) + ", "
                        + (persona.getNombre() == null ? "" : persona.getNombre());
        return new BonosDependenciaContent.Fila(legajoControl.getLegajoId(), apellidoNombre,
                contacto == null ? "" : contacto.getMailInstitucional(),
                legajoControl.getBonoEnviado() == 1);
    }
}
