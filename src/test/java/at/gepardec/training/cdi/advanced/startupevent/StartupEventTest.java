package at.gepardec.training.cdi.advanced.startupevent;

import java.lang.reflect.Method;

import at.gepardec.training.cdi.AbstractWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Covers the working "Startup Event" concept.
 * <p>
 * The CDI {@code @Observes @Initialized(ApplicationScoped.class)} chain became
 * {@code @EventListener(ApplicationReadyEvent.class)} on {@link StartupWebListener}, which then
 * fires a plain {@link StartupEvent} payload that {@link StartupObserver} observes synchronously.
 * <p>
 * The observer-id set that {@link StartupWebListener} logs at startup is NOT reachable afterwards -
 * the {@link StartupEvent} instance is a local variable of {@code contextInitialized()} and is
 * never published anywhere. So instead of asserting on unreachable state, the very same observer
 * chain is driven once more from the test with an event this test owns, which proves the wiring
 * that ran at startup. The startup path itself is pinned by reflection on the listener.
 * <p>
 * This test lives in the production package on purpose: {@code StartupEvent#observerIds()} and
 * {@code #add(String)} are package private, exactly as upstream.
 */
class StartupEventTest extends AbstractWebTest {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Test
    @DisplayName("the observer chain registers the observer id, synchronously")
    void theObserverChainRegistersItsId() {
        final StartupEvent event = new StartupEvent();

        publisher.publishEvent(event);

        // Synchronous: the id is already there when publishEvent returns, which is what lets
        // StartupWebListener log the observers immediately after firing.
        assertThat(event.observerIds()).containsExactly("StartupObserver");
    }

    @Test
    @DisplayName("the observer reports the unproxied class name")
    void theObserverReportsItsUnproxiedName() {
        // Util#nameWithoutProxy is used instead of getClass().getSimpleName() so the id stays
        // readable even if the bean ever ends up behind a CGLIB proxy.
        final StartupEvent event = new StartupEvent();

        publisher.publishEvent(event);

        assertThat(event.observerIds())
                .allSatisfy(id -> assertThat(id).doesNotContain("$$SpringCGLIB$$"));
    }

    @Test
    @DisplayName("the observed ids are exposed as an unmodifiable view")
    void theObserverIdsAreUnmodifiable() {
        final StartupEvent event = new StartupEvent();
        event.add("Something");
        event.add(null);

        assertThat(event.observerIds()).containsExactly("Something");
        assertThat(event.observerIds().getClass().getName()).contains("Unmodifiable");
    }

    @Test
    @DisplayName("the startup listener is bound to ApplicationReadyEvent and is not asynchronous")
    void theStartupListenerIsBoundToApplicationReadyEvent() throws Exception {
        final Method contextInitialized = StartupWebListener.class.getDeclaredMethod("contextInitialized");
        final EventListener listener = contextInitialized.getAnnotation(EventListener.class);

        assertThat(listener).isNotNull();
        assertThat(listener.value()).containsExactly(ApplicationReadyEvent.class);
        // Asynchronous firing would break the "Notified Observers: ..." log line right below it.
        assertThat(contextInitialized.getAnnotation(Async.class)).isNull();

        final Method observeStartup = StartupObserver.class.getDeclaredMethod("observeStartup", StartupEvent.class);
        assertThat(observeStartup.getAnnotation(EventListener.class)).isNotNull();
        assertThat(observeStartup.getAnnotation(Async.class)).isNull();
    }

    @Test
    @DisplayName("both startup beans are registered in the context")
    void bothStartupBeansAreRegistered() {
        assertThat(applicationContext.getBeanNamesForType(StartupWebListener.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(StartupObserver.class)).isNotEmpty();
    }

    @Test
    @DisplayName("the startup-event page renders")
    void thePageRenders() throws Exception {
        mockMvc.perform(get(API + "/advanced/startup-event"))
                .andExpect(status().isOk())
                .andExpect(view().name("advanced/startup-event"));

        assertThat(getHtml(API + "/advanced/startup-event")).contains("<h1>Startup Event</h1>");
    }
}
