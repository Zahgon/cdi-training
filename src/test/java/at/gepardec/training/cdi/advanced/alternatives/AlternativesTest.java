package at.gepardec.training.cdi.advanced.alternatives;

import java.util.Map;

import at.gepardec.training.cdi.AbstractWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the working "Alternatives" concept.
 * <p>
 * CDI expressed "this implementation wins over the default one" with {@code @Alternative} plus an
 * entry in {@code beans.xml}; the migration expresses it with {@code @Primary} on
 * {@link ServiceDev}. Both leave {@link ServiceProd} in the container, reachable through its
 * concrete type - which is exactly what the page demonstrates.
 */
class AlternativesTest extends AbstractWebTest {

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    private Map<String, Object> alternativesModel() throws Exception {
        return getModel(API + "/advanced/alternatives");
    }

    @Test
    @DisplayName("the Service interface resolves to the enabled alternative ServiceDev")
    void theInterfaceResolvesToTheAlternative() throws Exception {
        assertThat(modelValue(alternativesModel(), "result").toString()).startsWith("ServiceDev@");
    }

    @Test
    @DisplayName("the original ServiceProd is still a bean and reachable by its concrete type")
    void theOriginalImplementationIsStillAvailable() throws Exception {
        assertThat(modelValue(alternativesModel(), "resultOriginal").toString())
                .startsWith("ServiceProd@");
    }

    @Test
    @DisplayName("@Primary is what makes ServiceDev win the unqualified injection point")
    void theAlternativeIsThePrimaryServiceBean() {
        assertThat(applicationContext.getBeanNamesForType(Service.class))
                .contains("serviceDev", "serviceProd");
        assertThat(applicationContext.getBean(Service.class)).isInstanceOf(ServiceDev.class);
    }

    @Test
    @DisplayName("both implementations kept the request scope of the CDI original")
    void bothServicesAreRequestScoped() {
        assertThat(beanFactory.getBeanDefinition("scopedTarget.serviceDev").getScope())
                .isEqualTo("request");
        assertThat(beanFactory.getBeanDefinition("scopedTarget.serviceProd").getScope())
                .isEqualTo("request");
    }
}
