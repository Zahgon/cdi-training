package at.gepardec.training.cdi.basic.decorators;

import java.lang.reflect.Modifier;

import at.gepardec.training.cdi.AbstractWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Guards the training exercise "Decorators".
 * <p>
 * Upstream {@link ServiceDecorator} lacked {@code @Decorator} and {@code @Priority}, so the CDI
 * container never enabled it and {@link DecoratorsController} always received the plain
 * {@link ServiceImpl}. Spring has no decorator mechanism at all; the equivalent enabling step would
 * be to make the decorator a concrete {@code @Primary} bean delegating to {@link ServiceImpl}, and
 * that is deliberately not done.
 * <p>
 * These assertions must only be changed if the exercise itself is intentionally solved.
 */
class DecoratorsExerciseTest extends AbstractWebTest {

    @Test
    @DisplayName("BROKEN: the decorator is not a bean, so it can never wrap anything")
    void theDecoratorIsNotRegistered() {
        assertThat(applicationContext.getBeanNamesForType(ServiceDecorator.class)).isEmpty();
        // It cannot even be instantiated: the class stayed abstract and carries no stereotype.
        assertThat(Modifier.isAbstract(ServiceDecorator.class.getModifiers())).isTrue();
        assertThat(ServiceDecorator.class.getAnnotations()).isEmpty();
    }

    @Test
    @DisplayName("BROKEN: the only ServiceApi in the container is the undecorated ServiceImpl")
    void theServiceApiIsThePlainImplementation() {
        assertThat(applicationContext.getBeanNamesForType(ServiceApi.class))
                .containsExactly("serviceImpl");

        final ServiceApi service = applicationContext.getBean(ServiceApi.class);
        assertThat(service).isInstanceOf(ServiceImpl.class);
        assertThat(service).isNotInstanceOf(ServiceDecorator.class);
        // Not wrapped by anything either - no AOP proxy stands in for the missing decorator.
        assertThat(AopUtils.isAopProxy(service)).isFalse();
        assertThat(AopUtils.getTargetClass(service)).isEqualTo(ServiceImpl.class);
    }

    @Test
    @DisplayName("both service calls of the route go straight to ServiceImpl")
    void theRouteRendersWithTheUndecoratedService() throws Exception {
        // decorated() and nonDecorated() behave identically today; a working decorator would make
        // only the first one log a before/after pair.
        mockMvc.perform(get(API + "/basic/decorators"))
                .andExpect(status().isOk())
                .andExpect(view().name("basic/decorators"));

        assertThat(businessModelKeys(getModel(API + "/basic/decorators")))
                .containsExactlyInAnyOrder("cdiUri", "pathHelper");
    }
}
