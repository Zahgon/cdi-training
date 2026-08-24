package at.gepardec.training.cdi.basic.events;

import java.lang.reflect.Method;

import at.gepardec.training.cdi.AbstractWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Guards the training exercise "Events".
 * <p>
 * The observer side is complete - {@link EventObserver#observe(String)} is an {@code @Async}
 * {@code @EventListener}, the Spring counterpart of the CDI {@code @ObservesAsync} method - but the
 * firing side was never written: {@link EventsController} has no publisher and no state at all, so
 * the observer can never receive anything. Writing the publisher is the student's task.
 * <p>
 * The tests assert what is actually observable rather than inventing an event bus: that the
 * controller has no way of publishing, and that the page consequently renders nothing dynamic.
 * They must only be changed if the exercise itself is intentionally solved.
 */
class EventsExerciseTest extends AbstractWebTest {

    @Test
    @DisplayName("BROKEN: the controller holds no publisher, so it cannot fire an event")
    void theControllerHasNoPublisher() {
        assertThat(EventsController.class.getDeclaredFields()).isEmpty();
        assertThat(applicationContext.getBean(EventsController.class))
                .isNotNull();
        // Nothing in the class references the publisher type the fix would have to inject.
        assertThat(EventsController.class.getDeclaredMethods())
                .allSatisfy(method -> assertThat(method.getParameterTypes())
                        .doesNotContain(ApplicationEventPublisher.class));
    }

    @Test
    @DisplayName("BROKEN: the route renders a page without a single dynamic value")
    void theRouteRendersNothingDynamic() throws Exception {
        mockMvc.perform(get(API + "/basic/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("basic/events"));

        // Only the two layout beans are in the model; no event result ever reaches the view.
        assertThat(businessModelKeys(getModel(API + "/basic/events")))
                .containsExactlyInAnyOrder("cdiUri", "pathHelper");
    }

    @Test
    @DisplayName("the observer side is wired and waiting - it is the firing side that is missing")
    void theObserverIsRegisteredAndAsynchronous() throws Exception {
        // The bean name is explicit because a second EventObserver lives in the registrar package.
        assertThat(applicationContext.containsBean("basicEventObserver")).isTrue();
        assertThat(applicationContext.getBean("basicEventObserver")).isInstanceOf(EventObserver.class);

        final Method observe = EventObserver.class.getDeclaredMethod("observe", String.class);
        assertThat(observe.getAnnotation(EventListener.class))
                .as("@EventListener replaces the CDI @Observes")
                .isNotNull();
        assertThat(observe.getAnnotation(Async.class))
                .as("@Async replaces the CDI @ObservesAsync")
                .isNotNull();
    }
}
