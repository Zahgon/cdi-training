package at.gepardec.training.cdi.advanced.dynamicdefault;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Publishes the {@link Service} implementation chosen by the {@link ServiceProducer} as the bean
 * that unqualified injection points get.
 * <p>
 * The CDI producer method was annotated {@code @Produces @RequestScoped @Default}; the Spring
 * counterpart is a request scoped {@code @Bean} factory method, and {@code @Primary} takes the
 * role of the CDI {@code @Default} qualifier among the several {@link Service} beans.
 */
@Configuration
public class ServiceProducerConfiguration {

    @Bean
    @RequestScope
    @Primary
    public Service dynamicDefaultService(ServiceProducer serviceProducer) {
        return serviceProducer.createService();
    }
}
