package at.gepardec.training.cdi.basic.producers;

import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

/**
 * A CDI bean carrying producers becomes a Spring {@code @Configuration} class carrying
 * {@code @Bean} factory methods.
 */
@Configuration
public class Producer {

    /**
     * This is a producer field.
     * If no scope is provided then the bean is produced for the @Dependent scope
     * If no qualifier is provided then the @Default is used
     * <p>
     * Spring has no producer fields, so the constant is returned from a factory method
     * registered under the bean name {@code producedString}.
     */
    @Bean
    String producedString() {
        return "Hello, I got produced";
    }

    /**
     * This is a producer method for a normal scoped bean.
     * We can define Parameters which are CDI beans and CDi will provide them for us.
     */
    @Bean
    @RequestScope
    ProducedBean createProducedBean(Logger log) {
        log.info("Called BeanProducer#createProducedBean");
        return new ProducedBean();
    }
}
