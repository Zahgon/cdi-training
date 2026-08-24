package at.gepardec.training.cdi.advanced.specializes;

import java.util.Map;

import at.gepardec.training.cdi.AbstractWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the working "Specializes" concept and the single most subtle piece of the migration.
 * <p>
 * CDI {@code @Specializes} did two things: the specialised bean took over the qualifiers of its
 * superclass AND the superclass bean was disabled, so even an injection point declared with the
 * concrete supertype {@link ServiceOriginal} received the specialising bean. Spring has no such
 * annotation; {@code @Primary} on {@link ServiceSpecialized} reproduces it, because the subclass
 * also matches the {@link ServiceOriginal} type and wins the ambiguity there as well.
 * <p>
 * That is the difference to "Alternatives", where the original stays reachable by its concrete
 * type, and it is why both model values must name the specialising bean here.
 */
class SpecializesTest extends AbstractWebTest {

    private Map<String, Object> specializesModel() throws Exception {
        return getModel(API + "/advanced/specializes");
    }

    @Test
    @DisplayName("the Service interface resolves to ServiceSpecialized")
    void theInterfaceResolvesToTheSpecialisingBean() throws Exception {
        assertThat(modelValue(specializesModel(), "result").toString())
                .startsWith("ServiceSpecialized@");
    }

    @Test
    @DisplayName("even the ServiceOriginal injection point receives ServiceSpecialized")
    void theSupertypeInjectionPointAlsoReceivesTheSpecialisingBean() throws Exception {
        // This is what separates @Specializes from @Alternative; if the migration had only made
        // the bean primary for the interface, this value would read 'ServiceOriginal@...'.
        assertThat(modelValue(specializesModel(), "resultOriginal").toString())
                .startsWith("ServiceSpecialized@");
    }

    @Test
    @DisplayName("both values name the very same instance within one request")
    void bothValuesDescribeTheSameInstance() throws Exception {
        final Map<String, Object> model = specializesModel();

        assertThat(modelValue(model, "result")).isEqualTo(modelValue(model, "resultOriginal"));
    }

    @Test
    @DisplayName("ServiceOriginal is still registered, it just never wins")
    void theSpecialisedBeanIsStillInTheContainer() {
        assertThat(applicationContext.getBeanNamesForType(ServiceOriginal.class))
                .contains("serviceOriginal", "serviceSpecialized");
        assertThat(applicationContext.getBean(ServiceOriginal.class))
                .isInstanceOf(ServiceSpecialized.class);
        assertThat(applicationContext.getBean(Service.class))
                .isInstanceOf(ServiceSpecialized.class);
    }
}
