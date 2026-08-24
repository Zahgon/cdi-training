package at.gepardec.training.cdi.basic.scopes;

import java.util.Map;

import at.gepardec.training.cdi.AbstractWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guards the training exercise "Scopes".
 * <p>
 * Every bean in {@code at.gepardec.training.cdi.basic.scopes} carries the WRONG scope on purpose -
 * that is the exercise. The Jakarta EE original had {@code RequestBean} annotated
 * {@code @ApplicationScoped} and left the session and application beans with no scope at all, so
 * they were {@code @Dependent}. The migration reproduced that one to one: no scope annotation
 * became a Spring singleton, {@code @Dependent} became {@code prototype}.
 * <p>
 * The counters therefore behave the wrong way round, and these tests assert exactly that. They must
 * only be changed if the exercise itself is intentionally solved.
 * <p>
 * The singleton counters are shared with every other test in the suite, so they are asserted
 * RELATIVE to a first reading; only the values that must not move at all are asserted absolutely.
 */
class ScopesExerciseTest extends AbstractWebTest {

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    private static final String BASIC = "/basic/scopes/basic";

    private static final String MIXED = "/basic/scopes/mixed";

    private static final String DEPENDENT = "/basic/scopes/dependent";

    private int intValue(Map<String, Object> model, String key) {
        return ((Number) modelValue(model, key)).intValue();
    }

    private String scopeOf(String beanName) {
        final String scopedTarget = "scopedTarget." + beanName;
        final String name = beanFactory.containsBeanDefinition(scopedTarget) ? scopedTarget : beanName;
        final String scope = beanFactory.getBeanDefinition(name).getScope();
        return scope.isEmpty() ? BeanDefinition.SCOPE_SINGLETON : scope;
    }

    @Test
    @DisplayName("BROKEN: requestValue keeps counting up because RequestBean is a singleton")
    void requestValueIncrementsAcrossRequestsAlthoughItShouldNot() throws Exception {
        final int first = intValue(getModel(API + BASIC), "requestValue");
        final int second = intValue(getModel(API + BASIC), "requestValue");
        final int third = intValue(getModel(API + BASIC), "requestValue");

        // The page says "I should never change"; it does, because RequestBean has no scope
        // annotation and is therefore a singleton, the counterpart of the upstream
        // @ApplicationScoped.
        assertThat(second).isEqualTo(first + 1);
        assertThat(third).isEqualTo(first + 2);
    }

    @Test
    @DisplayName("BROKEN: sessionValue and applicationValue restart at 1 on every request")
    void sessionAndApplicationValuesNeverGrow() throws Exception {
        // SessionBean and ApplicationBean are prototypes - the migration of the upstream
        // @Dependent scope - so the request scoped controller gets a brand new counter each time.
        for (int i = 0; i < 3; i++) {
            final Map<String, Object> model = getModel(API + BASIC);
            assertThat(intValue(model, "sessionValue")).isEqualTo(1);
            assertThat(intValue(model, "applicationValue")).isEqualTo(1);
        }
    }

    @Test
    void theBasicScopesPageCarriesItsTabTitle() throws Exception {
        assertThat(modelValue(getModel(API + BASIC), "tabTitle")).isEqualTo("Scopes Basic");
    }

    @Test
    @DisplayName("BROKEN: the mixed page shows the same inverted picture")
    void mixedScopesShowTheSameInvertedBehaviour() throws Exception {
        final Map<String, Object> first = getModel(API + MIXED);
        final Map<String, Object> second = getModel(API + MIXED);

        assertThat(modelValue(first, "tabTitle")).isEqualTo("Mixed Scopes");
        // MixedRequestBean has no scope annotation -> singleton -> the "request" counter grows.
        assertThat(intValue(second, "requestValue")).isEqualTo(intValue(first, "requestValue") + 1);
        // MixedSessionBean is @RequestScope and MixedApplicationBean a prototype, so both restart.
        assertThat(intValue(first, "sessionValue")).isEqualTo(1);
        assertThat(intValue(second, "sessionValue")).isEqualTo(1);
        assertThat(intValue(first, "applicationValue")).isEqualTo(1);
        assertThat(intValue(second, "applicationValue")).isEqualTo(1);
    }

    @Test
    @DisplayName("BROKEN: a dependent bean inherits the wrong scope of the bean holding it")
    void dependentBeansInheritTheWrongLifetimeOfTheirHolder() throws Exception {
        final Map<String, Object> first = getModel(API + DEPENDENT);
        final Map<String, Object> second = getModel(API + DEPENDENT);

        // The DependentBean of the singleton RequestBean is created once and kept forever.
        assertThat(intValue(second, "dependentBeanRequestScopedValue"))
                .isEqualTo(intValue(first, "dependentBeanRequestScopedValue") + 1);
        // The holders of the other two are prototypes, so their DependentBean is new every time.
        assertThat(intValue(first, "dependentBeanSessionScopedValue")).isEqualTo(1);
        assertThat(intValue(second, "dependentBeanSessionScopedValue")).isEqualTo(1);
        assertThat(intValue(first, "dependentBeanApplicationScopedValue")).isEqualTo(1);
        assertThat(intValue(second, "dependentBeanApplicationScopedValue")).isEqualTo(1);
    }

    @Test
    @DisplayName("BROKEN: the bean definitions carry the wrong scopes, which is the root cause")
    void theBeanDefinitionsCarryTheWrongScopes() {
        assertThat(scopeOf("requestBean")).isEqualTo(BeanDefinition.SCOPE_SINGLETON);
        assertThat(scopeOf("sessionBean")).isEqualTo(BeanDefinition.SCOPE_PROTOTYPE);
        assertThat(scopeOf("applicationBean")).isEqualTo(BeanDefinition.SCOPE_PROTOTYPE);
        assertThat(scopeOf("scopesDependentBean")).isEqualTo(BeanDefinition.SCOPE_PROTOTYPE);

        assertThat(scopeOf("mixedRequestBean")).isEqualTo(BeanDefinition.SCOPE_SINGLETON);
        assertThat(scopeOf("mixedSessionBean")).isEqualTo("request");
        assertThat(scopeOf("mixedApplicationBean")).isEqualTo(BeanDefinition.SCOPE_PROTOTYPE);

        // Nothing in this package is session scoped, although two beans are named as if they were.
        assertThat(scopeOf("sessionBean")).isNotEqualTo("session");
        assertThat(scopeOf("mixedSessionBean")).isNotEqualTo("session");
    }

    @Test
    @DisplayName("the DependentBean bean name stayed explicit to avoid the initialization collision")
    void theDependentBeanKeepsItsExplicitName() {
        // Two classes are called DependentBean; the migration named them explicitly, otherwise the
        // component scan would have failed with a conflicting bean definition.
        assertThat(beanFactory.containsBeanDefinition("scopesDependentBean")).isTrue();
        assertThat(beanFactory.containsBeanDefinition("initializationDependentBean")).isTrue();
        assertThat(beanFactory.getBean("scopesDependentBean")).isInstanceOf(DependentBean.class);
    }
}
