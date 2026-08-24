package at.gepardec.training.cdi.advanced.registrar;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import at.gepardec.training.cdi.AbstractWebTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Covers the working "Registrar" concept - the only genuinely slow test of this suite.
 * <p>
 * CDI fired the event with {@code Event#fireAsync} and observed it with {@code @ObservesAsync};
 * the migration publishes through {@link org.springframework.context.ApplicationEventPublisher} and
 * observes with {@code @Async @EventListener}, which is enabled by {@code @EnableAsync} on the
 * application class. The registrar itself is the singleton holding the results.
 * <p>
 * The asynchrony is the behaviour under test, and the observer's own three second sleep is what
 * makes it observable: right after firing, the result must still be missing. The wait afterwards is
 * a bounded poll rather than a fixed sleep, and no polling library is added for it.
 * <p>
 * This test lives in the production package because {@code EventResultRegistrar} exposes its state
 * package private, exactly as upstream.
 */
class RegistrarAsyncEventTest extends AbstractWebTest {

    private static final String ROUTE = API + "/advanced/registrar";

    @Autowired
    private EventResultRegistrar registrar;

    @BeforeEach
    @AfterEach
    void clearTheRegistrar() throws Exception {
        mockMvc.perform(get(ROUTE + "/clear")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("a success event is observed asynchronously, a failed one is registered at once")
    void eventsAreObservedAsynchronouslyAndLandInTheMatchingSet() throws Exception {
        assertThat(registrar.successEvents()).isEmpty();
        assertThat(registrar.failedEvents()).isEmpty();

        final Map<String, Object> afterFiring = getModel(ROUTE + "/fire/successEvent");

        // Still empty although the request already completed: the observer sleeps three seconds
        // before registering, so a synchronous observer would have filled this set by now.
        assertThat(successEventsOf(afterFiring)).isEmpty();
        assertThat(registrar.successEvents()).isEmpty();

        pollUntil("the asynchronous observer to register the success event", 15_000L,
                () -> !registrar.successEvents().isEmpty());
        assertThat(registrar.successEvents()).hasSize(1);
        assertThat(registrar.failedEvents()).isEmpty();

        final String registeredId = registrar.successEvents().iterator().next();
        assertThat(successEventsOf(getModel(ROUTE))).containsExactly(registeredId);

        // The failing branch of the observer skips the sleep, so it may already be done here.
        getModel(ROUTE + "/fire/failedEvent");
        pollUntil("the asynchronous observer to register the failed event", 15_000L,
                () -> !registrar.failedEvents().isEmpty());

        assertThat(registrar.failedEvents()).hasSize(1);
        assertThat(registrar.successEvents()).hasSize(1);
        assertThat(registrar.failedEvents()).doesNotContainAnyElementsOf(registrar.successEvents());
        assertThat(failedEventsOf(getModel(ROUTE))).hasSize(1);
    }

    @Test
    @DisplayName("clear empties both sets and the page reflects it")
    void clearEmptiesBothSets() throws Exception {
        registrar.registerSuccessEvent("a-success-id");
        registrar.registerFailedEvent("a-failed-id");

        final Map<String, Object> beforeClear = getModel(ROUTE);
        assertThat(successEventsOf(beforeClear)).containsExactly("a-success-id");
        assertThat(failedEventsOf(beforeClear)).containsExactly("a-failed-id");

        final Map<String, Object> afterClear = getModel(ROUTE + "/clear");
        assertThat(successEventsOf(afterClear)).isEmpty();
        assertThat(failedEventsOf(afterClear)).isEmpty();
    }

    @Test
    @DisplayName("the registrar hands out copies, so the page can never mutate its state")
    void theRegistrarHandsOutDefensiveCopies() {
        registrar.registerSuccessEvent("an-id");

        final Set<String> snapshot = registrar.successEvents();
        registrar.registerSuccessEvent("another-id");

        assertThat(snapshot).containsExactly("an-id");
        assertThat(registrar.successEvents()).containsExactlyInAnyOrder("an-id", "another-id");
    }

    @Test
    @DisplayName("the observer is asynchronous and registered under its explicit bean name")
    void theObserverIsAsynchronousAndExplicitlyNamed() throws Exception {
        // The name is explicit because a second EventObserver lives in the basic events package.
        assertThat(applicationContext.containsBean("registrarEventObserver")).isTrue();

        final Method observe = EventObserver.class.getDeclaredMethod("observe", EventData.class);
        assertThat(observe.getAnnotation(Async.class))
                .as("@Async replaces the CDI @ObservesAsync")
                .isNotNull();
        assertThat(observe.getAnnotation(EventListener.class)).isNotNull();
        // Public because @Async is applied by a proxy that can only advise visible methods.
        assertThat(Modifier.isPublic(observe.getModifiers())).isTrue();
    }

    @Test
    @DisplayName("the registrar page renders both result lists")
    void thePageRendersBothResultLists() throws Exception {
        registrar.registerSuccessEvent("rendered-success");
        registrar.registerFailedEvent("rendered-failure");

        mockMvc.perform(get(ROUTE))
                .andExpect(status().isOk())
                .andExpect(view().name("advanced/registrar"));

        final String html = getHtml(ROUTE);
        assertThat(html).contains("<h1>Registrar (Pattern)</h1>");
        assertThat(html).contains("rendered-success");
        assertThat(html).contains("rendered-failure");
    }

    @SuppressWarnings("unchecked")
    private Set<String> successEventsOf(Map<String, Object> model) {
        return (Set<String>) modelValue(model, "successEvents");
    }

    @SuppressWarnings("unchecked")
    private Set<String> failedEventsOf(Map<String, Object> model) {
        return (Set<String>) modelValue(model, "failedEvents");
    }
}
