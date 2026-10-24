package um.haberes.report.contract;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Trazabilidad endpoint → test.
 *
 * <p>El manifiesto {@code endpoints.txt} dice que endpoints existen; este test exige que
 * <b>cada uno</b> tenga un metodo de test declarado en {@code coverage.txt}, que la clase y el
 * metodo existan y que el metodo este anotado con {@link Test}. Si se renombra un test o se
 * agrega un endpoint sin caracterizar, falla.
 */
class EndpointCoverageTest {

    @Test
    void cadaEndpointTieneUnTestDeCaracterizacion() throws ReflectiveOperationException {
        Map<String, String> cobertura = cobertura();

        assertThat(cobertura.keySet())
                .as("endpoints cubiertos por tests")
                .containsExactlyInAnyOrderElementsOf(manifiesto());

        for (Map.Entry<String, String> entry : cobertura.entrySet()) {
            String referencia = entry.getValue();
            int separator = referencia.indexOf('#');
            assertThat(separator).as("formato FQCN#metodo en '%s'", referencia).isPositive();

            Class<?> testClass = Class.forName(referencia.substring(0, separator));
            String methodName = referencia.substring(separator + 1);
            Method method = testClass.getDeclaredMethod(methodName);

            assertThat(method.isAnnotationPresent(Test.class))
                    .as("%s#%s debe ser un @Test", testClass.getSimpleName(), methodName)
                    .isTrue();
        }
    }

    private Map<String, String> cobertura() {
        Map<String, String> coverage = new LinkedHashMap<>();
        for (String line : lineas("/contract/coverage.txt")) {
            int separator = line.indexOf('|');
            assertThat(separator).as("formato 'ENDPOINT | FQCN#metodo' en '%s'", line).isPositive();
            coverage.put(line.substring(0, separator).trim(), line.substring(separator + 1).trim());
        }
        return coverage;
    }

    private Set<String> manifiesto() {
        return new TreeSet<>(lineas("/contract/endpoints.txt"));
    }

    private Set<String> lineas(String resource) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream(resource)))) {
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
            throw new UncheckedIOException("No se pudo leer " + resource, e);
        }
    }
}
