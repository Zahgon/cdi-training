package at.gepardec.training.cdi.basic.qualifiers;

import java.util.Map;

import at.gepardec.training.cdi.AbstractWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guards the training exercise "Qualifiers".
 * <p>
 * {@code QualifiersController} injects two {@link Shape} fields and expects the second one to be a
 * {@link Rectangle}. It is not, and that is the exercise: upstream {@code Rectangle} was
 * {@code @Alternative} without ever being enabled in {@code beans.xml}, so it was not a bean at all
 * and both injection points resolved to {@link Circle}. The migration expresses "disabled
 * alternative" as "carries no stereotype", which yields exactly the same result.
 * <p>
 * These assertions must only be changed if the exercise itself is intentionally solved.
 */
class QualifiersExerciseTest extends AbstractWebTest {

    @Test
    @DisplayName("BROKEN: both shapes are a Circle, the rectangle injection point is unqualified")
    void bothShapesResolveToCircle() throws Exception {
        final Map<String, Object> model = getModel(API + "/basic/qualifiers");

        assertThat(modelValue(model, "circle")).isEqualTo("Circle");
        assertThat(modelValue(model, "rectangle")).isEqualTo("Circle");
    }

    @Test
    @DisplayName("BROKEN: Rectangle is not registered, so it can never be injected")
    void rectangleIsNotABean() {
        // Adding @Component here would make Spring resolve the two Shape injection points by field
        // name and silently hand the controller a Rectangle - the exercise would disappear.
        assertThat(applicationContext.getBeanNamesForType(Rectangle.class)).isEmpty();
        assertThat(applicationContext.getBeanNamesForType(Circle.class)).isNotEmpty();
        assertThat(applicationContext.getBeanNamesForType(Shape.class))
                .allSatisfy(name -> assertThat(applicationContext.getBean(name)).isInstanceOf(Circle.class));
    }

    @Test
    @DisplayName("the page still tells the student what the values are supposed to become")
    void thePageStillStatesTheExpectedOutcome() throws Exception {
        final String html = getHtml(API + "/basic/qualifiers");

        assertThat(html).contains("I should still be a <code>Circle</code>");
        assertThat(html).contains("I should now be a <code>Rectangle</code>");
        // Both rendered values are 'Circle' today - the second one is the unsolved exercise.
        assertThat(html).contains("<span class=\"font-weight-bold\">Circle</span>");
        assertThat(html).doesNotContain(">Rectangle</span>");
    }
}
