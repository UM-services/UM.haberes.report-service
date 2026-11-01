package um.haberes.report.contract;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import um.haberes.report.support.AbstractReportIntegrationTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * La API key se exige <b>sólo</b> en el namespace nuevo del frontend
 * ({@code /api/haberes/report/ui/**}). Las rutas legacy que consume {@code bonos.vb6} siguen
 * respondiendo sin clave: es la regla de oro de {@code compras-service}.
 *
 * <p>Además de los casos puntuales, un test parametrizado recorre el manifiesto de endpoints y
 * verifica que <b>cada</b> ruta {@code /ui/**} responda 401 sin clave: una ruta nueva sin
 * protección rompe el build en lugar de descubrirse en producción.
 */
class ApiKeyFilterTest extends AbstractReportIntegrationTest {

    private static final String HEADER = "X-API-Key";
    private static final String API_KEY = "test-api-key";
    private static final String UI_PREFIX = "/api/haberes/report/ui/";
    private static final String UI_URL = "/api/haberes/report/ui/totalesGenerales/data/2026/9";
    private static final String LEGACY_URL =
            "/api/haberes/report/totalesGenerales/totalesGenerales/2026/9";

    @Test
    void rechazaSinApiKeyEnElNamespaceUi() throws Exception {
        MvcResult result = mockMvc.perform(get(UI_URL)).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(401);
        assertThat(result.getResponse().getContentAsString())
                .contains("Missing or invalid X-API-Key header");
    }

    @Test
    void aceptaConApiKeyValida() throws Exception {
        MvcResult result = mockMvc.perform(get(UI_URL).header(HEADER, API_KEY)).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
    }

    @Test
    void noExigeApiKeyEnLasRutasLegacy() throws Exception {
        MvcResult result = mockMvc.perform(get(LEGACY_URL)).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
    }

    static Stream<Arguments> endpointsDelNamespaceUi() {
        List<Arguments> arguments = new ArrayList<>();
        for (String linea : manifiesto()) {
            int separador = linea.indexOf(' ');
            String metodo = linea.substring(0, separador);
            String ruta = linea.substring(separador + 1);
            if (!ruta.startsWith(UI_PREFIX)) {
                continue;
            }
            arguments.add(Arguments.of(metodo, ruta.replaceAll("\\{[^}]+}", "1")));
        }
        return arguments.stream();
    }

    /**
     * Sin clave, el filtro corta antes de resolver el path o parsear el cuerpo: no hacen falta
     * stubs de los clientes ni valores válidos de los parámetros.
     */
    @ParameterizedTest(name = "{0} {1} sin clave -> 401")
    @MethodSource("endpointsDelNamespaceUi")
    void todaRutaUiExigeApiKey(String metodo, String ruta) throws Exception {
        MockHttpServletRequestBuilder request = "POST".equals(metodo) ? post(ruta) : get(ruta);

        MvcResult result = mockMvc.perform(request).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(401);
    }

    private static List<String> manifiesto() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                ApiKeyFilterTest.class.getResourceAsStream("/contract/endpoints.txt")))) {
            return reader.lines().map(String::trim).filter(linea -> !linea.isEmpty()).toList();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el manifiesto de endpoints", e);
        }
    }
}
