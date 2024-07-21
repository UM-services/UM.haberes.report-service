package um.haberes.report.configuration;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "um.haberes.report.client")
@PropertySource("classpath:config/haberes.properties")
public class ReportConfiguration {
}
