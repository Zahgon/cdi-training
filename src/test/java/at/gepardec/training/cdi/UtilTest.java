package at.gepardec.training.cdi;

import java.util.List;

import at.gepardec.training.cdi.basic.qualifiers.Circle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Plain unit test, no Spring context: {@link Util} is the display helper every controller uses to
 * turn a bean reference into the string shown on the page.
 * <p>
 * The proxy stripping is the one part the migration had to rewrite - Weld appended
 * {@code $Proxy$_$$_WeldClientProxy}, Spring appends {@code $$SpringCGLIB$$<n>} - so it is
 * exercised against a real Spring CGLIB proxy rather than a hand written string.
 */
class UtilTest {

    @Test
    @DisplayName("nameWithInstanceId strips the package but keeps the identity hash")
    void nameWithInstanceIdStripsThePackage() {
        final Circle circle = new Circle();

        final String name = Util.nameWithInstanceId(circle);

        assertThat(name)
                .isEqualTo("Circle@" + circle.hashCode())
                .doesNotContain("at.gepardec.training.cdi");
    }

    @Test
    @DisplayName("nameWithInstanceId works for a bean that does not override toString")
    void nameWithInstanceIdUsesTheDefaultToString() {
        final CdiUri cdiUri = new CdiUri();

        assertThat(Util.nameWithInstanceId(cdiUri))
                .isEqualTo("CdiUri@" + Integer.toHexString(cdiUri.hashCode()));
    }

    @Test
    @DisplayName("nameWithInstanceId leaves a toString that does not contain the package alone")
    void nameWithInstanceIdLeavesForeignToStringUntouched() {
        assertThat(Util.nameWithInstanceId("a plain string")).isEqualTo("a plain string");
    }

    @Test
    void nameWithInstanceIdRejectsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> Util.nameWithInstanceId(null))
                .withMessage("Cannot build the 'nameWithHashcode' with a null instance");
    }

    @Test
    @DisplayName("namesWithInstanceId maps every element of the collection")
    void namesWithInstanceIdMapsEveryElement() {
        final Circle first = new Circle();
        final Circle second = new Circle();

        assertThat(Util.namesWithInstanceId(List.of(first, second)))
                .containsExactly("Circle@" + first.hashCode(), "Circle@" + second.hashCode());
    }

    @Test
    void namesWithInstanceIdOfAnEmptyCollectionIsEmpty() {
        assertThat(Util.namesWithInstanceId(List.of())).isEmpty();
    }

    @Test
    void nameWithoutProxyOfAnUnproxiedBeanIsTheSimpleName() {
        assertThat(Util.nameWithoutProxy(new CdiUri())).isEqualTo("CdiUri");
    }

    @Test
    @DisplayName("nameWithoutProxy strips the Spring CGLIB suffix a scoped bean is wrapped in")
    void nameWithoutProxyStripsTheSpringCglibSuffix() {
        final ProxyFactory proxyFactory = new ProxyFactory(new CdiUri());
        proxyFactory.setProxyTargetClass(true);
        final Object proxy = proxyFactory.getProxy();

        // Guards the test itself: without a real CGLIB subclass there would be nothing to strip.
        assertThat(proxy.getClass().getSimpleName()).contains("$$SpringCGLIB$$");

        assertThat(Util.nameWithoutProxy(proxy)).isEqualTo("CdiUri");
    }

    @Test
    void nameWithoutProxyRejectsNull() {
        assertThatNullPointerException()
                .isThrownBy(() -> Util.nameWithoutProxy(null))
                .withMessage("Cannot build the 'nameWithHashcode' with a null instance");
    }
}
