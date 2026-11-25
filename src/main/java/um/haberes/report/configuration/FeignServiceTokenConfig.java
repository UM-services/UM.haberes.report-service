package um.haberes.report.configuration;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Propaga el token de servicio hacia haberes-core.
 *
 * <p>haberes-core exige `Authorization: Bearer` cuando
 * `app.seguridad.token-required=true`. El token se configura por entorno en
 * `HABERES_SERVICE_TOKEN` (el mismo valor que core declara en
 * `HABERES_SERVICE_TOKENS` como `report:<token>`).
 */
@Configuration
public class FeignServiceTokenConfig {

    @Value("${HABERES_SERVICE_TOKEN:}")
    private String serviceToken;

    @Bean
    public RequestInterceptor haberesServiceTokenInterceptor() {
        return template -> {
            if (serviceToken == null || serviceToken.isBlank()) {
                return;
            }
            if (template.headers().containsKey("Authorization")) {
                return;
            }
            template.header("Authorization", "Bearer " + serviceToken.trim());
        };
    }
}
