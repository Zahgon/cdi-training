package at.gepardec.training.cdi;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Shared base for every test that needs the real Spring context.
 * <p>
 * All web tests extend this class and add no further context configuration of their own, so the
 * Spring TestContext framework computes one single {@code MergedContextConfiguration} for the whole
 * suite and the application context is built exactly once and then cached.
 * <p>
 * {@code webEnvironment = MOCK} plus {@link AutoConfigureMockMvc} drives the real
 * {@code DispatcherServlet}, the real {@code ModelsMergingInterceptor}, the real
 * {@code GlobalExceptionHandler} and the real Thymeleaf rendering - nothing is mocked away, which
 * is the whole point: the tests have to prove the migrated wiring behaves like the Jakarta EE
 * original.
 * <p>
 * Note that MockMvc does not apply {@code server.servlet.context-path=/cditraining}, so every path
 * used in the tests starts at {@code /api} - the value of
 * {@link MvcApplication#REST_APPLICATION_PATH}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public abstract class AbstractWebTest {

    protected static final String API = MvcApplication.REST_APPLICATION_PATH;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ApplicationContext applicationContext;

    /**
     * Everything a bean wrote into the request scoped {@link Models} holder shows up in the
     * returned model, because {@link ModelsMergingInterceptor#postHandle} copies it over.
     */
    protected ModelAndView getModelAndView(String path) throws Exception {
        final MvcResult result = mockMvc.perform(get(path)).andReturn();
        final ModelAndView modelAndView = result.getModelAndView();
        assertThat(modelAndView)
                .as("no ModelAndView was produced for GET %s", path)
                .isNotNull();
        return modelAndView;
    }

    protected Map<String, Object> getModel(String path) throws Exception {
        return getModelAndView(path).getModel();
    }

    protected String getHtml(String path) throws Exception {
        return mockMvc.perform(get(path)).andReturn().getResponse().getContentAsString();
    }

    /**
     * The model keys a controller is responsible for. Spring MVC additionally puts a
     * {@code BindingResult} next to every {@code @ModelAttribute}, which says nothing about the
     * behaviour under test.
     */
    protected static Set<String> businessModelKeys(Map<String, Object> model) {
        return model.keySet().stream()
                .filter(key -> !key.startsWith(BindingResult.MODEL_KEY_PREFIX))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    protected Object modelValue(Map<String, Object> model, String key) {
        assertThat(model)
                .as("model attribute '%s' is missing; present keys: %s", key, model.keySet())
                .containsKey(key);
        return model.get(key);
    }

    /**
     * Bounded wait for the asynchronous observers. A polling library such as Awaitility is
     * deliberately not added - the suite has to run on {@code spring-boot-starter-test} alone.
     */
    protected static void pollUntil(String description, long timeoutMillis, ThrowingCondition condition) throws Exception {
        final long deadline = System.nanoTime() + timeoutMillis * 1_000_000L;
        while (System.nanoTime() < deadline) {
            if (condition.isMet()) {
                return;
            }
            Thread.sleep(50L);
        }
        assertThat(condition.isMet())
                .as("timed out after %d ms waiting for: %s", timeoutMillis, description)
                .isTrue();
    }

    @FunctionalInterface
    protected interface ThrowingCondition {
        boolean isMet() throws Exception;
    }
}
