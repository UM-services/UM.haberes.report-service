package um.haberes.report.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Congela el contrato de readiness que Consul usa para el registro del servicio.
 *
 * <p>Si alguien quita o renombra el endpoint, cambia el path del health check o deja
 * de exponer los probes, este test falla antes de que Consul pode una instancia sana
 * a los cinco minutos.
 */
class ConsulReadinessConfigTest {

    @Test
    void bootstrapDeclaraReadinessYProbesExplicitos() throws IOException {
        Map<String, Object> properties = loadBootstrapProperties();

        assertThat(properties)
                .containsEntry("spring.cloud.consul.discovery.health-check-path", "/actuator/health/readiness")
                .containsEntry("spring.cloud.consul.discovery.health-check-critical-timeout", "5m")
                .containsEntry("spring.cloud.consul.discovery.query-passing", true)
                .containsEntry("management.endpoint.health.probes.enabled", true)
                .containsEntry("management.endpoints.web.exposure.include", "health")
                .containsEntry("management.endpoint.health.show-details", "never");
    }

    private Map<String, Object> loadBootstrapProperties() throws IOException {
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        List<PropertySource<?>> sources = loader.load("bootstrap", new ClassPathResource("bootstrap.yml"));
        Map<String, Object> properties = new LinkedHashMap<>();
        sources.forEach(source -> flatten("", source.getSource(), properties));
        return properties;
    }

    private void flatten(String prefix, Object value, Map<String, Object> target) {
        if (value instanceof OriginTrackedValue tracked) {
            value = tracked.getValue();
        }
        if (value instanceof Map<?, ?> map) {
            map.forEach((key, nested) -> flatten(
                    prefix.isEmpty() ? key.toString() : prefix + "." + key,
                    nested,
                    target));
            return;
        }
        target.put(prefix, value);
    }
}
