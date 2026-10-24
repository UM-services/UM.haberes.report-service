package um.haberes.report.contract;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import um.haberes.report.support.AbstractReportIntegrationTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Evita que un endpoint quede sin caracterizar: compara el mapa real de Spring contra el
 * manifiesto versionado en {@code src/test/resources/contract/endpoints.txt}.
 *
 * <p>Si se agrega, borra o renombra un endpoint, este test falla antes que cualquier golden.
 */
class EndpointContractManifestTest extends AbstractReportIntegrationTest {

    @Test
    void todosLosEndpointsDeReportesEstanEnElManifiesto(
            @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping mapping) {
        Set<String> actuales = mapping.getHandlerMethods().entrySet().stream()
                .flatMap(entry -> endpoints(entry.getKey()).stream())
                .filter(endpoint -> endpoint.contains("/api/haberes/report/"))
                .collect(Collectors.toCollection(TreeSet::new));

        Set<String> esperados = manifiesto();

        assertThat(actuales)
                .as("endpoints publicados por los controllers de reportes")
                .containsExactlyInAnyOrderElementsOf(esperados);
    }

    private Set<String> endpoints(RequestMappingInfo info) {
        Set<String> methods = new LinkedHashSet<>();
        if (info.getMethodsCondition().getMethods().isEmpty()) {
            methods.add(RequestMethod.GET.name());
        } else {
            info.getMethodsCondition().getMethods().forEach(method -> methods.add(method.name()));
        }
        return info.getPatternValues().stream()
                .flatMap(pattern -> methods.stream().map(method -> method + " " + pattern))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<String> manifiesto() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/contract/endpoints.txt")))) {
            Set<String> lines = new TreeSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    lines.add(trimmed);
                }
            }
            return lines;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el manifiesto de endpoints", e);
        }
    }
}
