package at.gepardec.training.cdi.advanced.concurrency;

import java.util.Map;

import at.gepardec.training.cdi.AbstractWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the working "Concurrency" concept.
 * <p>
 * The point of the example is that a bean executed on a worker thread must not hold a proxy to a
 * request scoped bean, because the worker thread has no active request context. The solution is
 * unchanged by the migration: a request scoped holder ({@link ContextProducer}) creates an
 * immutable {@link Context} while the request context IS active, and a prototype factory method
 * hands that instance out without a scoped proxy in front of it.
 * <p>
 * The proof is the request URI showing up in the value the WORKER thread produced.
 */
class ConcurrencyTest extends AbstractWebTest {

    private static final String ROUTE = API + "/advanced/concurrency";

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @Test
    @DisplayName("the request URI survives the hop to the worker thread")
    void theRequestContextIsPropagatedToTheWorkerThread() throws Exception {
        final Map<String, Object> model = getModel(ROUTE);

        final String concurrentResult = modelValue(model, "concurrentResult").toString();
        assertThat(modelValue(model, "controllerResult")).isNotNull();
        // Service#execute renders '<threadId> (<requestUri>)'; had the Context been a request
        // scoped proxy, resolving it on the worker thread would have failed instead.
        assertThat(concurrentResult).matches("\\d+ \\(" + ROUTE + "\\)");
    }

    @Test
    @DisplayName("the work really happened on another thread than the request")
    void theServiceRunsOnADifferentThread() throws Exception {
        final Map<String, Object> model = getModel(ROUTE);

        final String controllerThreadId = modelValue(model, "controllerResult").toString();
        final String workerThreadId = modelValue(model, "concurrentResult").toString().split(" ")[0];

        assertThat(workerThreadId).isNotEqualTo(controllerThreadId);
    }

    @Test
    @DisplayName("every request gets its own Context carrying its own URI")
    void everyRequestGetsItsOwnContext() throws Exception {
        final String fromConcurrency = modelValue(getModel(ROUTE), "concurrentResult").toString();

        assertThat(fromConcurrency).contains("(" + ROUTE + ")");
        assertThat(beanFactory.getBeanDefinition("scopedTarget.contextProducer").getScope())
                .isEqualTo("request");
    }

    @Test
    @DisplayName("Context is handed out as a prototype, which is what makes it thread safe to pass")
    void theContextIsAPrototypeWithoutAScopedProxy() {
        // A request scoped @Bean would have produced a CGLIB proxy that re-resolves per call and
        // would blow up off the request thread; the prototype hands over the plain instance.
        assertThat(beanFactory.getBeanDefinition("createServiceConfig").isPrototype()).isTrue();
        assertThat(beanFactory.containsBeanDefinition("scopedTarget.createServiceConfig")).isFalse();
        assertThat(Context.class.getAnnotations()).isEmpty();
    }
}
