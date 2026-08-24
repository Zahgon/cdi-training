package at.gepardec.training.cdi.advanced.instance;

import java.util.List;
import java.util.Map;

import at.gepardec.training.cdi.AbstractWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the working "Instance&lt;T&gt;" concept - the CDI programmatic lookup.
 * <p>
 * {@code Instance<T>} became {@code ObjectProvider<T>}, {@code @Any} became "no qualifier on the
 * injection point", and {@code Instance#select(AnnotationLiteral)} became a lookup by qualifier
 * STRING through {@link SecondLiteral}. The page shows which beans each of those resolutions sees,
 * so the model is the honest place to assert type-hierarchy and qualifier filtering.
 */
class InstanceTest extends AbstractWebTest {

    @SuppressWarnings("unchecked")
    private Map<String, List<String>> instanceData() throws Exception {
        return (Map<String, List<String>>) modelValue(getModel(API + "/advanced/instance"), "data");
    }

    @Test
    @DisplayName("the page documents all seven lookups, in the declared order")
    void allSevenLookupsArePresent() throws Exception {
        assertThat(instanceData().keySet()).containsExactly(
                "@Inject @Any Instance<BeanInterfaceRoot>",
                "@Inject @Any Instance<BeanParent>",
                "@Inject Instance<BeanInterfaceChild>",
                "@Inject @Any Instance<BeanInterfaceChild>",
                "@Inject @Second Instance<BeanInterfaceChild>",
                "instance.select(new AnnotationLiteral<Default>(){})",
                "instance.select(new SecondLiteral())");
    }

    @Test
    @DisplayName("a root type lookup sees every bean in the hierarchy below it")
    void theRootTypeLookupSeesTheWholeHierarchy() throws Exception {
        assertThat(instanceData().get("@Inject @Any Instance<BeanInterfaceRoot>"))
                .containsExactlyInAnyOrder(
                        "BeanParent implements BeanInterfaceRoot",
                        "BeanChild implements BeanInterfaceChild",
                        "SecondBeanChild implements BeanInterfaceChild");
    }

    @Test
    @DisplayName("a concrete type lookup sees only that bean")
    void theConcreteTypeLookupSeesOnlyItsOwnBean() throws Exception {
        assertThat(instanceData().get("@Inject @Any Instance<BeanParent>"))
                .containsExactly("BeanParent implements BeanInterfaceRoot");
    }

    @Test
    @DisplayName("an unqualified child lookup sees only the @Default bean, @Any sees both")
    void theChildTypeLookupNarrowsTheHierarchy() throws Exception {
        final Map<String, List<String>> data = instanceData();

        // The implicit qualifier on an unqualified injection point is @Default, not @Any, so the
        // @Second-qualified bean is excluded here. This contrast is the whole point of the page:
        // if both rows agree, the @Default half of the lesson has been lost.
        assertThat(data.get("@Inject Instance<BeanInterfaceChild>"))
                .containsExactly("BeanChild implements BeanInterfaceChild");
        assertThat(data.get("@Inject @Any Instance<BeanInterfaceChild>"))
                .containsExactlyInAnyOrder(
                        "BeanChild implements BeanInterfaceChild",
                        "SecondBeanChild implements BeanInterfaceChild");
    }

    @Test
    @DisplayName("a qualified lookup filters the hierarchy down to the qualified bean")
    void theQualifiedLookupFiltersByTheSecondQualifier() throws Exception {
        assertThat(instanceData().get("@Inject @Second Instance<BeanInterfaceChild>"))
                .containsExactly("SecondBeanChild implements BeanInterfaceChild");
    }

    @Test
    @DisplayName("the programmatic default lookup resolves the @Primary bean")
    void theProgrammaticDefaultLookupResolvesThePrimaryBean() throws Exception {
        // CDI would have picked the @Default qualified bean; @Primary on BeanChild is the Spring
        // counterpart that breaks the tie between the two BeanInterfaceChild beans.
        assertThat(instanceData().get("instance.select(new AnnotationLiteral<Default>(){})"))
                .containsExactly("BeanChild implements BeanInterfaceChild");
    }

    @Test
    @DisplayName("the programmatic qualified lookup resolves through the bean name 'second'")
    void theProgrammaticQualifiedLookupResolvesTheSecondBean() throws Exception {
        assertThat(instanceData().get("instance.select(new SecondLiteral())"))
                .containsExactly("SecondBeanChild implements BeanInterfaceChild");
        assertThat(applicationContext.containsBean(SecondLiteral.QUALIFIER)).isTrue();
    }

    @Test
    @DisplayName("the default method of the most specific interface wins for the child beans")
    void theMostSpecificInterfaceProvidesTheName() {
        // BeanInterfaceChild overrides the default getName() of BeanInterfaceRoot; the rendered
        // names are what prove which interface each bean was resolved through.
        assertThat(new BeanChild().getName()).isEqualTo("BeanChild implements BeanInterfaceChild");
        assertThat(new BeanParent().getName()).isEqualTo("BeanParent implements BeanInterfaceRoot");
    }
}
