package um.haberes.report.reports.bono.infrastructure.web.dto;

import org.springframework.stereotype.Component;
import um.haberes.report.reports.bono.domain.model.BonosDependenciaContent;
import um.haberes.report.reports.bono.infrastructure.job.BonoPdfJob;

import java.util.List;
import java.util.Objects;

@Component
public class BonosDependenciaResponseMapper {

    public BonosDependenciaResponse toResponse(BonosDependenciaContent content) {
        if (content == null) {
            return new BonosDependenciaResponse(null, null, "", "", List.of());
        }
        List<BonosDependenciaResponse.Fila> filas = content.filas().stream()
                .filter(Objects::nonNull)
                .map(fila -> new BonosDependenciaResponse.Fila(text(fila.legajoId()),
                        text(fila.apellidoNombre()), text(fila.mailInstitucional()),
                        fila.bonoEnviado()))
                .toList();
        return new BonosDependenciaResponse(content.anho(), content.mes(),
                text(content.dependenciaId()), text(content.salida()), filas);
    }

    public BonoDependenciaPdfJobResponse toResponse(BonoPdfJob job) {
        return new BonoDependenciaPdfJobResponse(job.id(), job.estado().name(), job.procesados(),
                job.total(), job.mensaje(),
                job.fallidos().stream()
                        .filter(Objects::nonNull)
                        .map(fallido -> new BonoDependenciaPdfJobResponse.Fallido(
                                text(fallido.legajoId()), text(fallido.motivo())))
                        .toList());
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}