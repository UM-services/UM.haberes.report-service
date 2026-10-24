package um.haberes.report.reports.cargosclase;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import um.haberes.report.reports.cargosclase.application.usecases.GenerateCargosClaseReportUseCaseImpl;
import um.haberes.report.reports.cargosclase.domain.model.CargoClase;
import um.haberes.report.reports.cargosclase.domain.model.CargosClaseContent;
import um.haberes.report.reports.cargosclase.domain.ports.out.CargoClaseCatalogPort;
import um.haberes.report.reports.cargosclase.domain.ports.out.CargosClasePdfRenderer;
import um.haberes.report.shared.domain.model.ClaseResumen;
import um.haberes.report.shared.domain.model.ReportFile;
import um.haberes.report.shared.domain.ports.out.ClaseCatalogPort;
import um.haberes.report.shared.domain.ports.out.ReportStorage;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Caso de uso de "Cargos con Clase": agrupacion, orden y contrato de salida. */
class GenerateCargosClaseReportUseCaseTest {

    private final ClaseCatalogPort claseCatalogPort = mock(ClaseCatalogPort.class);
    private final CargoClaseCatalogPort cargoClaseCatalogPort = mock(CargoClaseCatalogPort.class);
    private final CargosClasePdfRenderer renderer = mock(CargosClasePdfRenderer.class);
    private final ReportStorage storage = mock(ReportStorage.class);
    private final GenerateCargosClaseReportUseCaseImpl useCase = new GenerateCargosClaseReportUseCaseImpl(
            claseCatalogPort, cargoClaseCatalogPort, renderer, storage);

    @Test
    void agrupaPorClaseOrdenaCargosYOmiteClasesSinCargos() {
        when(claseCatalogPort.findAll()).thenReturn(List.of(
                clase(2, "Segunda"), clase(1, "Primera"), clase(3, "Sin cargos")));
        when(cargoClaseCatalogPort.findAll()).thenReturn(List.of(
                new CargoClase(12L, "Ayudante de Primera", 1),
                new CargoClase(11L, "Jefe de Trabajos Prácticos", 1),
                new CargoClase(13L, null, 1),
                new CargoClase(21L, "Profesor Titular", 2),
                new CargoClase(99L, "Cargo Huérfano", 7)));
        Path target = Path.of("target/test-output/cargos-clase.pdf");
        when(storage.resolve("cargos-clase.pdf")).thenReturn(target);
        when(storage.load(target, "cargos-clase.pdf", ReportFile.Format.PDF))
                .thenReturn(new ReportFile(target, "cargos-clase.pdf", ReportFile.Format.PDF, 100));

        ReportFile result = useCase.generate();

        ArgumentCaptor<CargosClaseContent> captor = ArgumentCaptor.forClass(CargosClaseContent.class);
        verify(renderer).render(captor.capture(), eq(target));
        CargosClaseContent content = captor.getValue();

        assertThat(content.clases()).hasSize(2);
        assertThat(content.clases())
                .extracting(grupo -> grupo.clase().getClaseId())
                .containsExactly(2, 1);
        assertThat(content.clases().get(1).cargos())
                .extracting(CargoClase::cargoClaseId)
                .containsExactly(12L, 11L, 13L);
        assertThat(content.clases().get(0).cargos())
                .extracting(CargoClase::cargoClaseId)
                .containsExactly(21L);

        assertThat(result.displayName()).isEqualTo("cargos-clase.pdf");
        assertThat(result.format()).isEqualTo(ReportFile.Format.PDF);
        verify(storage).load(target, "cargos-clase.pdf", ReportFile.Format.PDF);
    }

    @Test
    void sinCargosNoGeneraSecciones() {
        when(claseCatalogPort.findAll()).thenReturn(List.of(clase(1, "Primera")));
        when(cargoClaseCatalogPort.findAll()).thenReturn(List.of());
        Path target = Path.of("target/test-output/cargos-clase.pdf");
        when(storage.resolve("cargos-clase.pdf")).thenReturn(target);
        when(storage.load(any(), any(), any()))
                .thenReturn(new ReportFile(target, "cargos-clase.pdf", ReportFile.Format.PDF, 0));

        useCase.generate();

        ArgumentCaptor<CargosClaseContent> captor = ArgumentCaptor.forClass(CargosClaseContent.class);
        verify(renderer).render(captor.capture(), eq(target));
        assertThat(captor.getValue().clases()).isEmpty();
    }

    private static ClaseResumen clase(Integer id, String nombre) {
        return ClaseResumen.builder().claseId(id).nombre(nombre).build();
    }
}
