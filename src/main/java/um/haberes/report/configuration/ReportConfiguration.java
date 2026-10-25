package um.haberes.report.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.time.Clock;

@Configuration
@EnableCaching
@EnableFeignClients(basePackages = "um.haberes.report.shared.infrastructure.client")
@PropertySource("classpath:config/haberes.properties")
public class ReportConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
