package at.gepardec.training.cdi.basic.inject;

import java.util.Map;

import at.gepardec.training.cdi.AbstractWebTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Guards the two training exercises "Inject field" and "Inject constructor".
 * <p>
 * Both routes are BROKEN ON PURPOSE and the student's task is to repair them. The assertions below
 * therefore pin the failure, not a success: they must only be changed if the exercises themselves
 * are intentionally solved. A green run here means the migration reproduced the Jakarta EE
 * behaviour, where the same two endpoints ended on the error page as well.
 * <p>
 * It doubles as the test for {@code GlobalExceptionHandler}, the {@code @ControllerAdvice} that
 * replaced the JAX-RS {@code ExceptionMapper}.
 */
class InjectExerciseTest extends AbstractWebTest {

    @Test
    @DisplayName("inject/field fails: the controller news up InjectModel, so its Models is null")
    void injectFieldEndsOnTheErrorPage() throws Exception {
        // InjectFieldController does 'new InjectModel()' instead of injecting it, so the container
        // never populates InjectModel#models and setForView dereferences null.
        final MvcResult result = mockMvc.perform(get(API + "/basic/inject/field"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error"))
                .andReturn();

        final Map<String, Object> model = result.getModelAndView().getModel();
        assertThat(modelValue(model, "exceptionType")).isEqualTo("java.lang.NullPointerException");
        assertThat(modelValue(model, "exceptionMessage").toString())
                .contains("at.gepardec.training.cdi.Models")
                .contains("is null");
        assertThat(modelValue(model, "uri").toString()).endsWith(API + "/basic/inject/field");
        assertThat(modelValue(model, "stackTrace").toString())
                .contains("at.gepardec.training.cdi.basic.inject.InjectModel.setForView");

        assertThat(result.getResponse().getContentAsString())
                .contains("java.lang.NullPointerException")
                .doesNotContain("your fix works");
    }

    @Test
    @DisplayName("inject/constructor fails: Spring picks the no-arg constructor, so model is null")
    void injectConstructorEndsOnTheErrorPage() throws Exception {
        // Neither constructor of InjectConstructorController is annotated, so Spring falls back to
        // the no-arg one and the InjectModel field stays null - the CDI container did the same,
        // because @Inject was missing on the constructor taking the model.
        final MvcResult result = mockMvc.perform(get(API + "/basic/inject/constructor"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error"))
                .andReturn();

        final Map<String, Object> model = result.getModelAndView().getModel();
        assertThat(modelValue(model, "exceptionType")).isEqualTo("java.lang.NullPointerException");
        assertThat(modelValue(model, "exceptionMessage").toString())
                .contains("at.gepardec.training.cdi.basic.inject.InjectModel.setForView")
                .contains("this.model")
                .contains("is null");
        assertThat(modelValue(model, "uri").toString()).endsWith(API + "/basic/inject/constructor");

        assertThat(result.getResponse().getContentAsString())
                .contains("java.lang.NullPointerException")
                .doesNotContain("your fix works");
    }

    @Test
    @DisplayName("the error page is decorated by the layout, which needs cdiUri and pathHelper")
    void theErrorPageCarriesTheLayoutBeans() throws Exception {
        // An @ExceptionHandler model is not enriched by @ModelAttribute methods of a
        // @ControllerAdvice, so GlobalExceptionHandler has to add these two itself; without them
        // the layout template would fail to render.
        final Map<String, Object> model = getModelAndView(API + "/basic/inject/field").getModel();

        assertThat(model).containsKeys("cdiUri", "pathHelper");
        assertThat(model.keySet())
                .containsExactlyInAnyOrder("exceptionType", "exceptionMessage", "uri", "stackTrace",
                        "cdiUri", "pathHelper");
    }

    @Test
    @DisplayName("InjectModel itself is wired correctly - only its two consumers are not")
    void injectModelIsAProperlyWiredBean() {
        // The exercise is in the controllers, not in the model: proving the bean is fine keeps the
        // two tests above honest about where the defect actually is.
        assertThat(applicationContext.getBeanNamesForType(InjectModel.class)).isNotEmpty();
        assertThat(applicationContext.getBean(InjectModel.class)).isNotNull();
    }
}
