package at.gepardec.training.cdi.basic.initialization;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import at.gepardec.training.cdi.AbstractWebTest;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Guards the training exercise "Initialization".
 * <p>
 * {@link BaseBean} offers {@code logInit()} and {@code logDestroy()} but neither carries a
 * lifecycle annotation, so no callback ever runs - upstream the {@code @PostConstruct} and
 * {@code @PreDestroy} annotations were missing in exactly the same way, and adding them is the
 * student's task. {@link InitializationController} additionally never touches the injected beans,
 * so the lazily created normal scoped bean is not even instantiated by a request.
 * <p>
 * These assertions must only be changed if the exercise itself is intentionally solved.
 */
class InitializationExerciseTest extends AbstractWebTest {

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @Test
    @DisplayName("BROKEN: logInit carries no @PostConstruct and logDestroy no @PreDestroy")
    void theLifecycleCallbacksAreNotAnnotated() throws Exception {
        final Method logInit = BaseBean.class.getDeclaredMethod("logInit");
        final Method logDestroy = BaseBean.class.getDeclaredMethod("logDestroy");

        assertThat(logInit.getAnnotation(PostConstruct.class)).isNull();
        assertThat(logDestroy.getAnnotation(PreDestroy.class)).isNull();
        assertThat(logInit.getAnnotations()).isEmpty();
        assertThat(logDestroy.getAnnotations()).isEmpty();
    }

    @Test
    @DisplayName("BROKEN: neither concrete bean adds the callbacks the base class is missing")
    void neitherSubclassDeclaresALifecycleMethod() {
        // Both classes are empty on purpose; the student is expected to annotate the inherited
        // methods rather than to reimplement them.
        assertThat(declaredMethodsOf(NormalScopedBean.class)).isEmpty();
        assertThat(declaredMethodsOf(DependentBean.class)).isEmpty();
    }

    /** Synthetic members are filtered out because the JaCoCo agent adds {@code $jacocoInit}. */
    private static List<Method> declaredMethodsOf(Class<?> type) {
        return Arrays.stream(type.getDeclaredMethods()).filter(method -> !method.isSynthetic()).toList();
    }

    @Test
    @DisplayName("the two lifetimes the exercise contrasts are wired: request scope vs prototype")
    void theTwoBeansKeepTheirContrastingLifetimes() {
        // NormalScopedBean stands for a CDI normal scope, DependentBean for the CDI @Dependent
        // scope; the exercise is about when their callbacks fire, so the scopes must stay intact.
        assertThat(beanFactory.getBeanDefinition("scopedTarget.normalScopedBean").getScope())
                .isEqualTo("request");
        assertThat(beanFactory.getBeanDefinition("initializationDependentBean").getScope())
                .isEqualTo("prototype");
    }

    @Test
    @DisplayName("BROKEN: the route renders without touching either injected bean")
    void theRouteRendersWithoutTouchingTheBeans() throws Exception {
        // The controller only returns the view name; nothing reaches Models, which is why the page
        // stays silent even once the callbacks are annotated but nothing forces instantiation.
        mockMvc.perform(get(API + "/basic/initialization"))
                .andExpect(status().isOk())
                .andExpect(view().name("basic/initialization"));

        assertThat(businessModelKeys(getModel(API + "/basic/initialization")))
                .containsExactlyInAnyOrder("cdiUri", "pathHelper");
    }
}
