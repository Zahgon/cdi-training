package at.gepardec.training.cdi.advanced.lookupfactory;

import at.gepardec.training.cdi.AbstractWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Covers the working "Lookup Factory" concept: a bean is selected by a runtime string.
 * <p>
 * CDI resolved it through {@code Instance#select} with an {@code AnnotationLiteral} carrying the
 * discriminator. Spring's programmatic lookup selects by qualifier STRING, and it cannot derive one
 * from {@code @TypedService("A")} because the {@code @Qualifier} meta annotation has no value - so
 * the migration additionally registers each implementation under its discriminator as the BEAN
 * NAME, which {@code BeanFactoryAnnotationUtils#qualifiedBeanOfType} matches on.
 */
class LookupFactoryTest extends AbstractWebTest {

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @ParameterizedTest(name = "GET /advanced/lookup-factory/{0} resolves {1}")
    @CsvSource({"A, ServiceA", "B, ServiceB"})
    void theTypeInThePathSelectsTheImplementation(String type, String expectedImplementation) throws Exception {
        final String result = modelValue(getModel(API + "/advanced/lookup-factory/" + type), "result").toString();

        assertThat(result).startsWith(expectedImplementation + "@");
    }

    @Test
    @DisplayName("the discriminator is the bean name, which is what makes the lookup work")
    void theImplementationsAreRegisteredUnderTheirDiscriminator() {
        assertThat(beanFactory.containsBeanDefinition("A")).isTrue();
        assertThat(beanFactory.containsBeanDefinition("B")).isTrue();
        assertThat(ServiceA.class.getAnnotation(TypedService.class).value()).isEqualTo("A");
        assertThat(ServiceB.class.getAnnotation(TypedService.class).value()).isEqualTo("B");
    }

    @Test
    @DisplayName("the two implementations keep the different scopes the factory has to tell apart")
    void theTwoImplementationsKeepTheirDifferentScopes() {
        // ServiceFactory only tracks the prototype ('@Dependent') instances for later destruction,
        // so the scopes are part of the behaviour, not an accident.
        assertThat(beanFactory.getBeanDefinition("scopedTarget.A").getScope()).isEqualTo("request");
        assertThat(beanFactory.getBeanDefinition("B").isPrototype()).isTrue();
    }

    @Test
    @DisplayName("an unknown type is rejected instead of silently resolving something")
    void anUnknownTypeFailsTheRequest() throws Exception {
        // The factory throws NoSuchBeanDefinitionException, which GlobalExceptionHandler turns
        // into the error page.
        mockMvc.perform(get(API + "/advanced/lookup-factory/C"))
                .andExpect(status().isInternalServerError());
    }
}
