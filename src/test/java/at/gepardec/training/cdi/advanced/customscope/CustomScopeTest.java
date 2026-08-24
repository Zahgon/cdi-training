package at.gepardec.training.cdi.advanced.customscope;

import java.util.List;
import java.util.Map;

import at.gepardec.training.cdi.AbstractWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Covers the custom scope, the deepest piece of the migration.
 * <p>
 * CDI let a portable extension contribute an {@code AlterableContext} for a {@code @NormalScope}
 * annotation. Spring has neither, so the migration re-expressed the whole mechanism: the context
 * became an implementation of the {@code Scope} SPI keyed by BEAN NAME instead of by
 * {@code Contextual}, the extension became a {@link org.springframework.beans.factory.config.CustomScopeConfigurer},
 * and the "normal scope" (proxyable) half became {@code ScopedProxyMode.TARGET_CLASS}.
 * <p>
 * The behavioural contract that has to survive all of that is exactly one sentence: within one
 * activation of the context, every lookup yields the same instance; a new activation yields a new
 * one. That is what the two executions on the page demonstrate and what is asserted here.
 */
class CustomScopeTest extends AbstractWebTest {

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @SuppressWarnings("unchecked")
    private Map<String, List<String>> executions() throws Exception {
        return (Map<String, List<String>>) modelValue(getModel(API + "/advanced/customscope"), "data");
    }

    @Test
    @DisplayName("the page shows the two executions the controller performs, in order")
    void thePageShowsBothExecutions() throws Exception {
        assertThat(executions().keySet()).containsExactly("Execution 1", "Execution 2");
        assertThat(executions().values()).allSatisfy(ids -> assertThat(ids).hasSize(3));
    }

    @Test
    @DisplayName("within one execution every lookup yields the very same IdService instance")
    void theIdIsStableWithinOneExecution() throws Exception {
        final Map<String, List<String>> executions = executions();

        for (Map.Entry<String, List<String>> execution : executions.entrySet()) {
            assertThat(execution.getValue())
                    .as("the three lookups of '%s' must resolve one and the same bean", execution.getKey())
                    .hasSize(3)
                    .allMatch(id -> id.matches("IdService\\{id=[0-9a-f\\-]{36}}"))
                    .containsOnly(execution.getValue().get(0));
        }
    }

    @Test
    @DisplayName("a second execution activates a fresh context and therefore a fresh instance")
    void theIdDiffersAcrossExecutions() throws Exception {
        final Map<String, List<String>> executions = executions();

        // This is the assertion that proves the Spring Scope really replaced the CDI context: a
        // plain singleton would repeat the id here, a prototype would already have failed above.
        assertThat(executions.get("Execution 1").get(0))
                .isNotEqualTo(executions.get("Execution 2").get(0));
    }

    @Test
    @DisplayName("two requests never share an execution scoped instance either")
    void theIdDiffersAcrossRequests() throws Exception {
        final Map<String, List<String>> first = executions();
        final Map<String, List<String>> second = executions();

        assertThat(first.get("Execution 1")).doesNotContainAnyElementsOf(second.get("Execution 1"));
        assertThat(first.get("Execution 2")).doesNotContainAnyElementsOf(second.get("Execution 2"));
    }

    @Test
    @DisplayName("IdService is bound to the registered 'execution' scope through a client proxy")
    void theIdServiceIsBoundToTheExecutionScope() {
        assertThat(ExecutionContextExtension.SCOPE_NAME).isEqualTo("execution");
        assertThat(IdService.class.getAnnotation(ExecutionScoped.class)).isNotNull();
        assertThat(beanFactory.getRegisteredScope(ExecutionContextExtension.SCOPE_NAME))
                .isSameAs(ExecutionContextExtension.CONTEXT_SINGLETON);
        assertThat(beanFactory.getBeanDefinition("scopedTarget.idService").getScope())
                .isEqualTo(ExecutionContextExtension.SCOPE_NAME);
    }

    @Test
    @DisplayName("outside an activation the context refuses to hand out an instance")
    void lookingUpOutsideAnActivationFails() {
        // The Spring counterpart of the CDI ContextNotActiveException; the aspect around
        // WithExecutionScopeService is what normally opens the context.
        assertThat(ExecutionContextExtension.CONTEXT_SINGLETON.isActive()).isFalse();

        assertThatThrownBy(() -> applicationContext.getBean(IdService.class).getId())
                .hasRootCauseInstanceOf(IllegalStateException.class)
                .hasRootCauseMessage("No active context for current Thread");
    }

    @Test
    @DisplayName("the aspect closes the context again when the annotated call returns")
    void theAspectDeactivatesTheContextAfterTheCall() throws Exception {
        // MockMvc runs the request on this very thread and the scope is thread bound, so a missing
        // deactivation in the aspect's finally block would be visible right here.
        getModel(API + "/advanced/customscope");

        assertThat(ExecutionContextExtension.CONTEXT_SINGLETON.isActive()).isFalse();
        assertThat(ExecutionContextExtension.CONTEXT_SINGLETON.getConversationId()).isNull();
    }

    @Test
    @DisplayName("the aspect is enabled, unlike the two interceptors of the basic exercise")
    void theExecutionScopeAspectIsRegistered() {
        // Upstream this interceptor WAS listed in beans.xml, which is why - in contrast to
        // FirstInterceptor and SecondInterceptor - it carries @Aspect and @Component here.
        assertThat(applicationContext.getBeanNamesForType(WithExecutionScopeInterceptor.class))
                .isNotEmpty();
        assertThat(WithExecutionScopeService.class.getAnnotation(WithExecutionScope.class)).isNotNull();
    }
}
