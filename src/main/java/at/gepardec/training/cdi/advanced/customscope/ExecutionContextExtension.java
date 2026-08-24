package at.gepardec.training.cdi.advanced.customscope;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is the configuration registering our execution context.
 * <p>
 * It replaces the CDI portable extension that was registered through
 * {@code META-INF/services/jakarta.enterprise.inject.spi.Extension}. Spring has no container lifecycle events such as
 * {@code AfterBeanDiscovery}, so the two things the extension did are expressed as beans instead: the context is
 * contributed with a {@link CustomScopeConfigurer} rather than {@code AfterBeanDiscovery#addContext(Context)}, and the
 * programmatically added bean becomes a plain {@code @Bean} factory method.
 */
@Configuration
public class ExecutionContextExtension {

    /**
     * The scope name the {@link ExecutionScoped} beans are bound to.
     */
    public static final String SCOPE_NAME = "execution";

    /**
     * There is exactly one context instance in the container, which differentiates active contexts via its
     * ThreadLocal instances, so it is held here just like the CDI extension held it.
     */
    public static final ExecutionContextImpl CONTEXT_SINGLETON = new ExecutionContextImpl();

    /**
     * Register the execution-context instance.
     * <p>
     * The method is {@code static} because a {@link CustomScopeConfigurer} is a {@code BeanFactoryPostProcessor} and
     * has to be created before the enclosing configuration class is instantiated.
     */
    @Bean
    public static CustomScopeConfigurer executionScopeConfigurer() {
        final CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope(SCOPE_NAME, CONTEXT_SINGLETON);

        return configurer;
    }

    /**
     * Register a Spring wrapper over the execution-context, for controlling the execution context activity.
     */
    @Bean
    public ExecutionContextController executionContextController() {
        return new ExecutionContextControllerImpl(CONTEXT_SINGLETON);
    }

}
