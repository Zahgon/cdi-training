package at.gepardec.training.cdi;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Proves every route of the migrated application is reachable and renders the Thymeleaf template
 * that replaced its Facelets counterpart.
 * <p>
 * The two {@code /basic/inject/*} routes are missing here on purpose: they are training exercises
 * that fail by design and are pinned in
 * {@code at.gepardec.training.cdi.basic.inject.InjectExerciseTest}.
 */
class SmokeRouteTest extends AbstractWebTest {

    static Stream<Arguments> routesAndViews() {
        return Stream.of(
                Arguments.of(API + "/", "welcome"),
                Arguments.of(API + "/welcome", "welcome"),
                Arguments.of(API + "/basic/index", "basic/index"),
                Arguments.of(API + "/basic/scopes/basic", "basic/scopes-basic"),
                Arguments.of(API + "/basic/scopes/dependent", "basic/scopes-dependent"),
                Arguments.of(API + "/basic/scopes/mixed", "basic/scopes-mixed"),
                Arguments.of(API + "/basic/initialization", "basic/initialization"),
                Arguments.of(API + "/basic/qualifiers", "basic/qualifiers"),
                Arguments.of(API + "/basic/producers", "basic/producers"),
                Arguments.of(API + "/basic/interceptors", "basic/interceptors"),
                Arguments.of(API + "/basic/decorators", "basic/decorators"),
                Arguments.of(API + "/basic/events", "basic/events"),
                Arguments.of(API + "/advanced/index", "advanced/index"),
                Arguments.of(API + "/advanced/alternatives", "advanced/alternatives"),
                Arguments.of(API + "/advanced/concurrency", "advanced/concurrency"),
                Arguments.of(API + "/advanced/customscope", "advanced/customscope"),
                Arguments.of(API + "/advanced/dynamic-default", "advanced/dynamic-default"),
                // 'ServiceTwo' is the producer's own default, so exercising the {type} route here
                // leaves the application scoped ServiceProducer state untouched for other tests.
                Arguments.of(API + "/advanced/dynamic-default/ServiceTwo", "advanced/dynamic-default"),
                Arguments.of(API + "/advanced/instance", "advanced/instances"),
                Arguments.of(API + "/advanced/lookup-factory/A", "advanced/lookup-factory"),
                Arguments.of(API + "/advanced/lookup-factory/B", "advanced/lookup-factory"),
                Arguments.of(API + "/advanced/registrar", "advanced/registrar"),
                Arguments.of(API + "/advanced/registrar/clear", "advanced/registrar"),
                Arguments.of(API + "/advanced/specializes", "advanced/specializes"),
                Arguments.of(API + "/advanced/startup-event", "advanced/startup-event"));
    }

    @ParameterizedTest(name = "GET {0} renders {1}")
    @MethodSource("routesAndViews")
    void routeRendersItsTemplate(String path, String expectedView) throws Exception {
        final MvcResult result = mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(view().name(expectedView))
                .andReturn();

        final String html = result.getResponse().getContentAsString();
        assertThat(html)
                .as("GET %s must render the decorated layout", path)
                .contains("<title>")
                .contains("navbar-container")
                .contains("Gepardec IT Services");
    }

    @Test
    @DisplayName("the /fire/{fail} route accepts an event and drains it again")
    void fireRouteIsReachableAndLeavesTheRegistrarEmpty() throws Exception {
        // 'failedEvent' is used because that observer branch registers without the 3s sleep of the
        // success branch, so the asynchronous work can be drained deterministically before the
        // registrar suite runs.
        mockMvc.perform(get(API + "/advanced/registrar/fire/failedEvent"))
                .andExpect(status().isOk())
                .andExpect(view().name("advanced/registrar"));

        pollUntil("the fired event to be registered", 10_000L,
                () -> !failedEventsOf(getModel(API + "/advanced/registrar")).isEmpty());

        mockMvc.perform(get(API + "/advanced/registrar/clear")).andExpect(status().isOk());
        assertThat(failedEventsOf(getModel(API + "/advanced/registrar"))).isEmpty();
    }

    @SuppressWarnings("unchecked")
    private Set<String> failedEventsOf(Map<String, Object> model) {
        return (Set<String>) modelValue(model, "failedEvents");
    }

    @Test
    @DisplayName("the welcome page renders its own heading and the CDI links")
    void welcomePageRendersItsContent() throws Exception {
        final MvcResult result = mockMvc.perform(get(API + "/welcome")).andReturn();
        final String html = result.getResponse().getContentAsString();

        assertThat(html).contains("<h1>CDI Training</h1>");
        assertThat(html).contains("Welcome to the CDI-Training");
        // cdiUri is contributed by GlobalModelAttributes; without it the layout would blow up.
        assertThat(html).contains("https://cdi-spec.org");
        assertThat(html).contains("CDI 3.0 Spec");
    }

    @Test
    @DisplayName("the basic index page lists every basic concept")
    void basicIndexListsTheConcepts() throws Exception {
        final String html = mockMvc.perform(get(API + "/basic/index"))
                .andReturn().getResponse().getContentAsString();

        assertThat(html).contains(
                "<div class=\"fs-3\">Injection</div>",
                "<div class=\"fs-3\">Scopes</div>",
                "<div class=\"fs-3\">Initialization</div>",
                "<div class=\"fs-3\">Qualifiers</div>",
                "<div class=\"fs-3\">Producers</div>",
                "<div class=\"fs-3\">Interceptors</div>",
                "<div class=\"fs-3\">Decorators</div>",
                "<div class=\"fs-3\">Events</div>");
    }

    @Test
    @DisplayName("the advanced index page lists every advanced concept")
    void advancedIndexListsTheConcepts() throws Exception {
        final String html = mockMvc.perform(get(API + "/advanced/index"))
                .andReturn().getResponse().getContentAsString();

        assertThat(html).contains(
                "<div class=\"fs-3\">Concurrency</div>",
                "<div class=\"fs-3\">Dynamic Default</div>",
                "<div class=\"fs-3\">Alternatives</div>",
                "<div class=\"fs-3\">Specializes</div>",
                "<div class=\"fs-3\">Registrar</div>",
                "<div class=\"fs-3\">Startup Event</div>",
                "<div class=\"fs-3\">Lookup Factory</div>",
                "<div class=\"fs-3\">Custom Scope</div>");
    }

    @Test
    @DisplayName("resources kept their WildFly URLs and the context path is applied by the layout")
    void layoutBuildsTheContextRootedResourceUrls() throws Exception {
        final String html = mockMvc.perform(get(API + "/welcome"))
                .andReturn().getResponse().getContentAsString();

        // PathHelper#buildResourcePath keeps the /resources/... URLs the Jakarta EE deployment had;
        // MockMvc runs with an empty context path, so the prefix is exactly '/resources'.
        assertThat(html).contains("src=\"/resources/img/cdi-logo.png\"");
        assertThat(html).contains("href=\"/resources/css/style.css\"");
    }

    @Test
    @DisplayName("the bare application path still dies on null path info, exactly as it did upstream")
    void bareApplicationPathIsTheUpstreamNullPointerQuirk() throws Exception {
        // Upstream the JAX-RS servlet is mapped to /api/*, so HttpServletRequest#getPathInfo()
        // returns null for the bare /cditraining/api and PathHelper dies on it. Spring's
        // DispatcherServlet is mapped to / and has to derive the path info; returning "" there
        // instead of null silently repairs the upstream bug and turns this into a welcome page.
        // The NPE escapes to the caller instead of reaching @ControllerAdvice because it is
        // thrown while the shared layout renders, not while the controller runs.
        assertThatThrownBy(() -> mockMvc.perform(get(API)))
                .rootCause()
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("String.endsWith(String)")
                .hasMessageContaining("is null");

        // The trailing-slash form is the one that works upstream, and must keep working.
        mockMvc.perform(get(API + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"));
    }
}
