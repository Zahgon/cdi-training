package at.gepardec.training.cdi.advanced.concurrency;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Exposes the {@link Context} held by the request scoped {@link ContextProducer} as a bean.
 * <p>
 * A CDI {@code @Produces} method is itself a bean; the Spring counterpart is a {@code @Bean}
 * factory method on a configuration class. The prototype scope replaces the CDI {@code @Dependent}
 * scope, so the {@link Context} is handed out without a scoped proxy in front of it - which is
 * exactly what makes it usable on another thread.
 */
@Configuration
public class ContextProducerConfiguration {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Context createServiceConfig(ContextProducer contextProducer) {
        return contextProducer.createServiceConfig();
    }
}
