package at.gepardec.training.cdi.basic.interceptors;

import at.gepardec.training.cdi.AbstractWebTest;

import org.aspectj.lang.annotation.Aspect;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Guards the training exercise "Interceptors".
 * <p>
 * Upstream neither interceptor carried {@code @Priority} nor was listed in the
 * {@code <interceptors>} section of {@code beans.xml}, so the CDI container never enabled them and
 * they never fired. The migration reproduces that by leaving {@code @Aspect} and {@code @Component}
 * off both classes: the advice exists in source but is never woven.
 * <p>
 * These assertions must only be changed if the exercise itself is intentionally solved.
 */
class InterceptorsExerciseTest extends AbstractWebTest {

    @Test
    @DisplayName("BROKEN: neither interceptor is a Spring bean, so nothing can be intercepted")
    void theInterceptorsAreNotRegistered() {
        assertThat(applicationContext.getBeanNamesForType(FirstInterceptor.class)).isEmpty();
        assertThat(applicationContext.getBeanNamesForType(SecondInterceptor.class)).isEmpty();
        assertThat(applicationContext.getBeanNamesForType(BaseInterceptor.class)).isEmpty();
    }

    @Test
    @DisplayName("BROKEN: neither interceptor carries @Aspect or @Component")
    void theInterceptorsCarryNoAspectStereotype() {
        // Adding both annotations is the Spring equivalent of adding @Priority and listing the
        // interceptor in beans.xml - that is precisely the step the student has to take.
        for (Class<?> interceptor : new Class<?>[]{FirstInterceptor.class, SecondInterceptor.class}) {
            assertThat(interceptor.getAnnotation(Aspect.class))
                    .as("%s must stay un-woven", interceptor.getSimpleName())
                    .isNull();
            assertThat(interceptor.getAnnotation(Component.class))
                    .as("%s must stay unregistered", interceptor.getSimpleName())
                    .isNull();
        }
    }

    @Test
    @DisplayName("the bindings are on the controller, they simply have nothing bound to them")
    void theControllerStillCarriesTheInterceptorBindings() {
        // The exercise is only about enabling the interceptors, so the bindings must stay in place.
        assertThat(InterceptorsController.class.getAnnotation(FirstIntercept.class)).isNotNull();
        assertThat(InterceptorsController.class.getAnnotation(SecondIntercept.class)).isNotNull();
    }

    @Test
    @DisplayName("the route renders although no advice runs around the handler")
    void theRouteRendersWithoutAnyInterception() throws Exception {
        mockMvc.perform(get(API + "/basic/interceptors"))
                .andExpect(status().isOk())
                .andExpect(view().name("basic/interceptors"));

        // Nothing is written into Models, so only the two globally contributed layout beans are
        // in the rendering model - an interceptor writing into it would show up here.
        assertThat(businessModelKeys(getModel(API + "/basic/interceptors")))
                .containsExactlyInAnyOrder("cdiUri", "pathHelper");
    }
}
