package um.haberes.report.reports.anotador.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.anotador.domain.model.AnotadorContent;
import um.haberes.report.reports.anotador.domain.model.AnotadorContent.Registro;
import um.haberes.report.reports.anotador.domain.model.AnotadorRegistro;
import um.haberes.report.reports.anotador.domain.ports.in.GenerateAnotadorReportUseCase;
import um.haberes.report.reports.anotador.domain.ports.out.AnotadorCatalogPort;
import um.haberes.report.reports.anotador.domain.ports.out.AnotadorPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GenerateAnotadorReportUseCaseImpl implements GenerateAnotadorReportUseCase {

    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final AnotadorCatalogPort anotadorCatalogPort;
    private final AnotadorPdfRenderer renderer;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer facultadId, Integer anho, Integer mes, boolean pendientes) {
        List<AnotadorRegistro> registros;
        List<AnotadorRegistro> rechazados = List.of();
        if (pendientes) {
            registros = facultadId == null
                    ? anotadorCatalogPort.findPendientes(anho, mes)
                    : anotadorCatalogPort.findPendientesByFacultad(facultadId, anho, mes);
        } else if (facultadId == null) {
            List<AnotadorRegistro> revisados = anotadorCatalogPort.findRevisados(anho, mes);
            registros = revisados.stream().filter(r -> r.autorizado() == 1).toList();
            rechazados = revisados.stream().filter(r -> r.rechazado() == 1).toList();
        } else {
            registros = anotadorCatalogPort.findAutorizadosByFacultad(facultadId, anho, mes);
            rechazados = anotadorCatalogPort.findRechazadosByFacultad(facultadId, anho, mes);
        }

        Path target = storage.resolve(fileName(facultadId, anho, mes, pendientes));
        renderer.render(new AnotadorContent(anho, mes, pendientes,
                registros.stream().map(this::toRegistro).toList(),
                rechazados.stream().map(this::toRegistro).toList()), target);
        return storage.load(target, displayName(pendientes), ReportFile.Format.PDF);
    }

    private Registro toRegistro(AnotadorRegistro registro) {
        return new Registro(text(registro.legajoId()), text(registro.documento()),
                text(registro.apellidoNombre()), text(registro.facultad()),
                fecha(registro.created()), text(registro.user()), estado(registro),
                text(registro.anotacion()), text(registro.respuesta()));
    }

    private String estado(AnotadorRegistro registro) {
        if (registro.autorizado() == 0 && registro.rechazado() == 0) {
            return "Pendiente";
        }
        if (registro.autorizado() == 1) {
            return "Autorizado";
        }
        if (registro.rechazado() == 1) {
            return "Rechazado";
        }
        return "Unknown";
    }

    private String fecha(LocalDateTime created) {
        return created == null ? "" : FECHA.format(created);
    }

    private String text(Object value) {
        return Objects.toString(value, "");
    }

    private String displayName(boolean pendientes) {
        return pendientes ? "anotadorPendientes.pdf" : "anotadorAutorizadosRechazados.pdf";
    }

    private String fileName(Integer facultadId, Integer anho, Integer mes, boolean pendientes) {
        if (pendientes) {
            return facultadId == null
                    ? "anotador.periodo." + anho + "." + mes + ".pdf"
                    : "anotador." + facultadId + "." + anho + "." + mes + ".pdf";
        }
        return facultadId == null
                ? "anotador.revisados." + anho + "." + mes + ".pdf"
                : "anotador.revisados." + facultadId + "." + anho + "." + mes + ".pdf";
    }
}
