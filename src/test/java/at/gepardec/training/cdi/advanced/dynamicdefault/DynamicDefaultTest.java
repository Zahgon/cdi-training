package at.gepardec.training.cdi.advanced.dynamicdefault;

import at.gepardec.training.cdi.AbstractWebTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Covers the working "Dynamic Default" concept: a factory method decides at runtime which
 * implementation the unqualified injection points receive.
 * <p>
 * CDI used a {@code @Produces @RequestScoped @Default} method; the migration uses a
 * {@code @Bean @RequestScope @Primary} method on {@link ServiceProducerConfiguration}, with
 * {@code @Primary} taking the role of the CDI {@code @Default} qualifier among the three
 * {@link Service} beans.
 * <p>
 * {@link ServiceProducer} is a singleton holding the selected type, so the state SURVIVES a
 * request. Every test therefore sets the type explicitly instead of relying on the previous one,
 * and the {@code @AfterEach} restores the initial value so nothing leaks into the rest of the suite.
 */
class DynamicDefaultTest extends AbstractWebTest {

    private static final String ROUTE = API + "/advanced/dynamic-default";

    @Autowired
    private ServiceProducer serviceProducer;

    @AfterEach
    void restoreTheInitialImplementationType() {
        serviceProducer.setImplementationType("ServiceTwo");
    }

    private String resultOf(String path) throws Exception {
        return modelValue(getModel(path), "result").toString();
    }

    @Test
    @DisplayName("ServiceTwo is the implementation the producer starts with")
    void serviceTwoIsTheInitialDefault() {
        // Asserted on the producer rather than through a request, because a request would first
        // have to change the very state that is under test.
        assertThat(serviceProducer.getImplementationType()).isEqualTo("ServiceTwo");
    }

    @Test
    @DisplayName("the unqualified injection point receives the currently selected implementation")
    void theDefaultRouteRendersTheSelectedImplementation() throws Exception {
        serviceProducer.setImplementationType("ServiceTwo");

        assertThat(resultOf(ROUTE)).startsWith("ServiceTwo@");
    }

    @Test
    @DisplayName("switching to ServiceOne changes what the default injection point resolves to")
    void switchingToServiceOneTakesEffectWithinTheSameRequest() throws Exception {
        // The controller resolves the ObjectProvider AFTER switching, which is what makes the
        // change visible in the very response that triggered it.
        assertThat(resultOf(ROUTE + "/ServiceOne")).startsWith("ServiceOne@");
        assertThat(serviceProducer.getImplementationType()).isEqualTo("ServiceOne");
    }

    @Test
    @DisplayName("the switch is remembered for the following requests and can be switched back")
    void theSelectionSurvivesTheRequestAndCanBeReverted() throws Exception {
        assertThat(resultOf(ROUTE + "/ServiceOne")).startsWith("ServiceOne@");
        assertThat(resultOf(ROUTE)).startsWith("ServiceOne@");

        assertThat(resultOf(ROUTE + "/ServiceTwo")).startsWith("ServiceTwo@");
        assertThat(resultOf(ROUTE)).startsWith("ServiceTwo@");
    }

    @Test
    @DisplayName("both implementations stay reachable through their own qualifiers")
    void bothImplementationsKeepTheirQualifiers() {
        assertThat(ServiceOne.class.getAnnotation(ServiceOneQualifier.class)).isNotNull();
        assertThat(ServiceTwo.class.getAnnotation(ServiceTwoQualifier.class)).isNotNull();
        assertThat(applicationContext.getBeanNamesForType(Service.class))
                .contains("serviceOne", "serviceTwo", "dynamicDefaultService");
    }

    @Test
    @DisplayName("an unknown implementation type is rejected by the producer")
    void anUnknownTypeIsRejected() {
        serviceProducer.setImplementationType("ServiceThree");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(serviceProducer::createService)
                .withMessage("implementationType unknown. type: ServiceThree");
    }
}
