package um.haberes.report.reports.librosueldos.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.haberes.report.reports.librosueldos.domain.ports.in.GenerateLibroSueldosLsdUseCase;
import um.haberes.report.shared.application.exception.ReportBusinessException;
import um.haberes.report.shared.application.exception.ReportException;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.LibroSueldosZipPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * ZIP del Libro de Sueldos Digital: lo genera haberes-core con los legajos visibles y acá sólo
 * se persiste para entregarlo con el mismo contrato de descarga que el resto de los reportes.
 */
@Component
@RequiredArgsConstructor
public class GenerateLibroSueldosLsdUseCaseImpl implements GenerateLibroSueldosLsdUseCase {

    static final String DISPLAY_NAME = "LSD.zip";

    private final LibroSueldosZipPort libroSueldosZipPort;
    private final ReportStorage storage;

    @Override
    public ReportFile generate(Integer anho, Integer mes, List<Long> legajoIds) {
        if (legajoIds == null || legajoIds.isEmpty()) {
            throw new ReportBusinessException("No hay legajos para generar el Libro de Sueldos Digital");
        }
        byte[] zip = libroSueldosZipPort.generate(anho, mes, legajoIds);
        Path target = storage.resolve("libro.sueldos." + anho + "." + mes + ".zip");
        try {
            Files.write(target, zip);
        } catch (IOException e) {
            throw new ReportException("No se pudo guardar el Libro de Sueldos Digital", e);
        }
        return storage.load(target, DISPLAY_NAME, ReportFile.Format.ZIP);
    }
}
