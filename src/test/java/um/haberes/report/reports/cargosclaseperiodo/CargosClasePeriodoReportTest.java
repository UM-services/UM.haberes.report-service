package um.haberes.report.reports.cargosclaseperiodo;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDetalleDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClaseDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.CargoClasePeriodoDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.ClaseDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.FacultadDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.GeograficaDto;
import um.haberes.report.shared.infrastructure.client.haberes.core.dto.PersonaDto;
import um.haberes.report.support.AbstractReportIntegrationTest;
import um.haberes.report.support.Fixtures;
import um.haberes.report.support.GoldenFiles;
import um.haberes.report.support.ReportHttp;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Reporte "Cargos con Clase por Periodo"
 * ({@code GET /cargosClasePeriodo/pdf/{facultadId}/{anho}/{mes}}).
 *
 * <p>Contrato: PDF attachment {@code cargos-clase-periodo.pdf}. Contenido: una seccion por
 * sede, ordenadas alfabeticamente por nombre (C4), con una fila por cargo del docente; se
 * omiten los detalles sin periodo o sin cargo clase asociado.
 */
class CargosClasePeriodoReportTest extends AbstractReportIntegrationTest {

    private static final String URL = "/api/haberes/report/cargosClasePeriodo/pdf/2/2026/9";

    @Test
    void generaPdfConCargosAgrupadosPorSede() throws Exception {
        JsonNode fixture = Fixtures.node("cargosclaseperiodo", "normal");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "cargos-clase-periodo.pdf");
        GoldenFiles.assertPdf("cargosclaseperiodo", "normal", pdf);
    }

    /**
     * Sin personas para la facultad se emite un PDF valido con el encabezado y la leyenda
     * "Sin datos para el período." (C1), en lugar del archivo de 0 bytes del legacy.
     */
    @Test
    void generaPdfConLeyendaSinPersonas() throws Exception {
        JsonNode fixture = Fixtures.node("cargosclaseperiodo", "vacio");
        stub(fixture);

        MvcResult result = mockMvc.perform(get(URL)).andReturn();

        byte[] pdf = ReportHttp.assertAttachment(result, "cargos-clase-periodo.pdf");
        GoldenFiles.assertPdf("cargosclaseperiodo", "vacio", pdf);
    }

    private void stub(JsonNode fixture) {
        when(facultadClient.findByFacultadId(2))
                .thenReturn(Fixtures.one(fixture, "facultad", FacultadDto.class));
        when(personaClient.findAllByFacultad(2))
                .thenReturn(Fixtures.list(fixture, "personas", PersonaDto.class));
        when(geograficaClient.findAll())
                .thenReturn(Fixtures.list(fixture, "geograficas", GeograficaDto.class));
        when(claseClient.findAll())
                .thenReturn(Fixtures.list(fixture, "clases", ClaseDto.class));

        for (PersonaDto persona : Fixtures.list(fixture, "personas", PersonaDto.class)) {
            Long legajoId = persona.getLegajoId();
            when(cargoClaseDetalleClient.findAllByLegajo(legajoId, 2026, 9))
                    .thenReturn(Fixtures.list(fixture, "detalles" + legajoId, CargoClaseDetalleDto.class));
            when(cargoClasePeriodoClient.findAllByLegajo(legajoId))
                    .thenReturn(Fixtures.list(fixture, "periodos" + legajoId, CargoClasePeriodoDto.class));
        }

        for (CargoClaseDto cargoClase : Fixtures.list(fixture, "cargoClases", CargoClaseDto.class)) {
            when(cargoClaseClient.findByCargoClaseId(cargoClase.getCargoClaseId())).thenReturn(cargoClase);
        }
    }
}
