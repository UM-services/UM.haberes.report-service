package um.haberes.report.reports.bono.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.bono.domain.model.BonoDependenciaItem;
import um.haberes.report.reports.bono.domain.model.BonosDependenciaLoteResultado;
import um.haberes.report.reports.bono.domain.model.BonosDependenciaLoteResultado.Fallido;
import um.haberes.report.reports.bono.domain.ports.in.BonoLoteProgreso;
import um.haberes.report.reports.bono.domain.ports.in.GenerateBonosDependenciaLoteUseCase;
import um.haberes.report.reports.bono.domain.ports.out.BonoDataPort;
import um.haberes.report.reports.bono.domain.ports.out.BonoPdfRenderer;
import um.haberes.report.reports.bono.domain.ports.out.PdfMergePort;
import um.haberes.report.shared.application.exception.ReportBusinessException;
import um.haberes.report.shared.application.exception.ReportException;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Genera el PDF consolidado de una dependencia con una sola lectura al core.
 *
 * <p>Cada legajo se renderiza a un archivo propio del job (para no pisar corridas simultáneas)
 * y al final se unen. Un legajo que falla se informa en {@code fallidos} sin cortar el lote.
 * Los PDFs individuales se borran siempre al terminar.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateBonosDependenciaLoteUseCaseImpl implements GenerateBonosDependenciaLoteUseCase {

    private final BonoDataPort dataPort;
    private final BonoContentAssembler assembler;
    private final BonoPdfRenderer renderer;
    private final PdfMergePort pdfMerge;
    private final ReportStorage storage;

    @Override
    public BonosDependenciaLoteResultado generar(Integer anho, Integer mes, Integer dependenciaId,
                                                 String salida, String jobId,
                                                 BonoLoteProgreso progreso) {
        List<BonoDependenciaItem> legajos = dataPort.cargarDependencia(anho, mes, dependenciaId,
                salida);
        if (legajos.isEmpty()) {
            throw new ReportBusinessException("No hay liquidaciones para la dependencia "
                    + dependenciaId + " en " + mes + "/" + anho);
        }

        int total = legajos.size();
        int procesados = 0;
        List<Path> bonos = new ArrayList<>(total);
        List<Fallido> fallidos = new ArrayList<>();
        if (progreso != null) {
            progreso.avanzar(0, total);
        }
        try {
            for (BonoDependenciaItem legajo : legajos) {
                Path target = storage.resolve("bono." + jobId + "." + legajo.legajoId() + "."
                        + anho + "." + mes + ".pdf");
                try {
                    BonoContentAssembler.Calculado calculado = assembler.recibo(legajo.fuente(),
                            anho, mes);
                    renderer.render(calculado.content(), target);
                    dataPort.guardarLetra(legajo.legajoId(), anho, mes, calculado.neto(),
                            calculado.cadena());
                    bonos.add(target);
                } catch (RuntimeException e) {
                    borrar(target);
                    fallidos.add(new Fallido(legajo.legajoId(), mensaje(e)));
                    log.warn("No se pudo generar el bono del legajo {} de la dependencia {}: {}",
                            legajo.legajoId(), dependenciaId, e.getMessage(), e);
                }
                procesados++;
                if (progreso != null) {
                    progreso.avanzar(procesados, total);
                }
            }

            if (bonos.isEmpty()) {
                throw new ReportBusinessException("No se pudo generar ningún bono de la dependencia "
                        + dependenciaId + " en " + mes + "/" + anho);
            }
            Path target = storage.resolve("dependencia." + dependenciaId + "." + jobId + ".pdf");
            pdfMerge.merge(target, bonos);
            ReportFile archivo = storage.load(target,
                    "bonos-" + dependenciaId + "-" + anho + "-" + mes + ".pdf",
                    ReportFile.Format.PDF);
            return new BonosDependenciaLoteResultado(archivo, total, fallidos);
        } finally {
            bonos.forEach(this::borrar);
        }
    }

    private String mensaje(RuntimeException e) {
        if (e instanceof ReportException && e.getMessage() != null) {
            return e.getMessage();
        }
        return "No se pudo generar el bono";
    }

    private void borrar(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.debug("No se pudo borrar el temporal {}: {}", path, e.getMessage());
        }
    }
}
