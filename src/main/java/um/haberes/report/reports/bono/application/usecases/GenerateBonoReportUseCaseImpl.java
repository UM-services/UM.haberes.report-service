package um.haberes.report.reports.bono.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.bono.domain.model.BonoFuente;
import um.haberes.report.reports.bono.domain.ports.in.GenerateBonoReportUseCase;
import um.haberes.report.reports.bono.domain.ports.out.BonoDataPort;
import um.haberes.report.reports.bono.domain.ports.out.BonoPdfRenderer;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class GenerateBonoReportUseCaseImpl implements GenerateBonoReportUseCase {

    private final BonoDataPort dataPort;
    private final BonoContentAssembler assembler;
    private final BonoPdfRenderer renderer;
    private final ReportStorage storage;
    private final Clock clock;

    @Override
    public ReportFile generar(Long legajoId, Integer anho, Integer mes, Long legajoIdSolicitud,
                              String ipAddress, boolean auditar) {
        dataPort.deleteItemsEnCero(legajoId, anho, mes);
        BonoFuente fuente = dataPort.cargar(legajoId, anho, mes, null);
        BonoContentAssembler.Calculado calculado = assembler.recibo(fuente, anho, mes);
        dataPort.guardarLetra(legajoId, anho, mes, calculado.neto(), calculado.cadena());

        Path target = storage.resolve(fileName(legajoId, anho, mes));
        renderer.render(calculado.content(), target);
        ReportFile file = storage.load(target, "bono.pdf", ReportFile.Format.PDF);
        if (auditar) {
            dataPort.auditar(legajoId, anho, mes, legajoIdSolicitud, ipAddress,
                    OffsetDateTime.now(clock).truncatedTo(ChronoUnit.MILLIS));
        }
        return file;
    }

    @Override
    public String nombreArchivo(Long legajoId, Integer anho, Integer mes) {
        BonoFuente.Persona persona = dataPort.persona(legajoId);
        String apellido = persona == null ? "" : sanitizar(persona.apellido());
        String nombre = persona == null ? "" : sanitizar(persona.nombre());
        return apellido + "." + nombre + "." + legajoId + "." + anho + "." + mes + ".pdf";
    }

    private String sanitizar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replaceAll("[\\\\/:*?\"<>|\\r\\n\\t]", "-").trim();
    }

    private String fileName(Long legajoId, Integer anho, Integer mes) {
        return "bono." + legajoId + "." + anho + "." + mes + ".pdf";
    }
}
