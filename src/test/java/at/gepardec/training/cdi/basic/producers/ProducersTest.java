package at.gepardec.training.cdi.basic.producers;

import java.util.List;
import java.util.Map;

import at.gepardec.training.cdi.AbstractWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the "Producers" concept, which is half working and half a training exercise.
 * <p>
 * Working: the CDI producer field and producer method became {@code @Bean} factory methods on the
 * {@code @Configuration} class {@link Producer}, and both reach the controller.
 * <p>
 * Exercise: the two list producers were never written, the controller hands empty lists to the
 * view. The tests below pin BOTH halves; the empty-list assertions must only be changed if the
 * exercise itself is intentionally solved.
 */
class ProducersTest extends AbstractWebTest {

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    private Map<String, Object> producersModel() throws Exception {
        return getModel(API + "/basic/producers");
    }

    @Test
    @DisplayName("the producer field became a @Bean and its exact value reaches the view")
    void theProducedStringReachesTheView() throws Exception {
        assertThat(modelValue(producersModel(), "producedString")).isEqualTo("Hello, I got produced");
    }

    @Test
    @DisplayName("the producer method became a request scoped @Bean and its instance reaches the view")
    void theProducedBeanReachesTheView() throws Exception {
        final Object producedBean = modelValue(producersModel(), "producedBean");

        // Util#nameWithInstanceId renders '<SimpleName>@<identity>'; the package must be gone and
        // the CGLIB suffix of the request scoped proxy must not leak into the page.
        assertThat(producedBean.toString())
                .startsWith("ProducedBean@")
                .doesNotContain("at.gepardec.training.cdi")
                .doesNotContain("$$SpringCGLIB$$");
    }

    @Test
    @DisplayName("BROKEN: circleList and rectangleList are empty, the list producers are missing")
    @SuppressWarnings("unchecked")
    void theListProducersAreStillMissing() throws Exception {
        // ProducersController literally puts Util.namesWithInstanceId(List.of()) into the model.
        // Writing 'List<Shape> createCircles()' and 'List<Shape> createRectangles()' in Producer
        // and injecting them is the student's task.
        final Map<String, Object> model = producersModel();

        assertThat((List<String>) modelValue(model, "circleList")).isEmpty();
        assertThat((List<String>) modelValue(model, "rectangleList")).isEmpty();
    }

    @Test
    @DisplayName("ProducedBean is only reachable through the factory method, never scanned")
    void producedBeanIsNotAScannedComponent() {
        // Upstream this was expressed with @Vetoed because bean-discovery-mode was 'all'; the
        // Spring counterpart is the absence of any stereotype on the class.
        assertThat(ProducedBean.class.getAnnotations()).isEmpty();
        assertThat(applicationContext.getBeanNamesForType(ProducedBean.class))
                .contains("createProducedBean")
                .allSatisfy(name -> assertThat(name).endsWith("createProducedBean"));
    }

    @Test
    @DisplayName("the produced string is registered under the name the injection point qualifies")
    void theProducedStringBeanIsNamedAfterItsFactoryMethod() {
        // ProducersController injects it with @Qualifier("producedString"); Spring matches that
        // string against the bean name, which is where the CDI @Default qualifier ended up.
        assertThat(applicationContext.containsBean("producedString")).isTrue();
        assertThat(applicationContext.getBean("producedString")).isEqualTo("Hello, I got produced");
    }

    @Test
    @DisplayName("the produced bean kept the request scope of the CDI producer method")
    void theProducedBeanIsRequestScoped() {
        // @Bean @RequestScope replaced @Produces @RequestScoped; the scoped proxy that goes with it
        // is what lets a singleton injection point resolve a fresh instance per request.
        assertThat(beanFactory.containsBeanDefinition("scopedTarget.createProducedBean")).isTrue();
        assertThat(beanFactory.getBeanDefinition("scopedTarget.createProducedBean").getScope())
                .isEqualTo("request");
        assertThat(applicationContext.getBean("createProducedBean").getClass().getName())
                .contains("$$SpringCGLIB$$");
    }
}
