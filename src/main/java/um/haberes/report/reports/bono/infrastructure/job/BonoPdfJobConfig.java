package um.haberes.report.reports.bono.infrastructure.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/** Pool acotado para las generaciones de PDF por dependencia. */
@Configuration
public class BonoPdfJobConfig {

    @Bean(name = "bonoPdfJobExecutor", destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor bonoPdfJobExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("bono-pdf-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
