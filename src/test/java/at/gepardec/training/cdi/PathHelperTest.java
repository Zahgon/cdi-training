package at.gepardec.training.cdi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Plain unit test, no Spring context: {@link PathHelper} is the bean every template asks for the
 * URLs, so it decides whether the migrated application keeps the URLs the WildFly deployment had.
 * <p>
 * {@code pathInfo()} is the part the migration had to rewrite. Under JAX-RS the servlet was mapped
 * to {@code /api/*} and {@code HttpServletRequest#getPathInfo()} returned the remainder; Spring's
 * DispatcherServlet is mapped to {@code /}, so the value is now derived from the request URI. Both
 * branches of that derivation are covered here.
 */
class PathHelperTest {

    private static final String CONTEXT_PATH = "/cditraining";

    private static PathHelper pathHelperFor(String requestUri) {
        final MockServletContext servletContext = new MockServletContext();
        servletContext.setContextPath(CONTEXT_PATH);
        final MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
        request.setContextPath(CONTEXT_PATH);
        request.setRequestURI(requestUri);
        return new PathHelper(request);
    }

    private static PathHelper pathHelper() {
        return pathHelperFor(CONTEXT_PATH + "/api/welcome");
    }

    @Test
    void basePathIsTheContextPathPlusTheApplicationPath() {
        assertThat(pathHelper().basePath()).isEqualTo("/cditraining/api");
    }

    @Test
    void resourcePathKeepsTheWildFlyResourcesLocation() {
        assertThat(pathHelper().resourcePath()).isEqualTo("/cditraining/resources");
    }

    @ParameterizedTest(name = "buildPath(\"{0}\") is \"#\"")
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("a blank path becomes the '#' no-op link")
    void buildPathOfBlankIsHash(String blank) {
        assertThat(pathHelper().buildPath(blank)).isEqualTo("#");
    }

    @ParameterizedTest(name = "buildPath(\"{0}\") is unchanged")
    @ValueSource(strings = {
            "#solution",
            "#anchor",
            "http://example.org/x",
            "https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#events"})
    @DisplayName("anchors and absolute URLs are passed through untouched")
    void buildPathPassesAnchorsAndAbsoluteUrlsThrough(String path) {
        assertThat(pathHelper().buildPath(path)).isEqualTo(path);
    }

    @ParameterizedTest(name = "buildPath(\"{0}\") is \"{1}\"")
    @CsvSource({
            "/x,                      /cditraining/api/x",
            "/basic/index,            /cditraining/api/basic/index",
            "/advanced/lookup-factory/A, /cditraining/api/advanced/lookup-factory/A"})
    void buildPathPrefixesApplicationRelativePaths(String path, String expected) {
        assertThat(pathHelper().buildPath(path)).isEqualTo(expected);
    }

    @Test
    void buildResourcePathPrefixesTheResourcesLocation() {
        assertThat(pathHelper().buildResourcePath("/img/cdi-logo.png"))
                .isEqualTo("/cditraining/resources/img/cdi-logo.png");
    }

    @Test
    void buildContextRootedPathPrefixesOnlyTheContextPath() {
        assertThat(pathHelper().buildContextRootedPath("/webjars/mdb-ui-kit/3.9.0/css/mdb.min.css"))
                .isEqualTo("/cditraining/webjars/mdb-ui-kit/3.9.0/css/mdb.min.css");
    }

    @Test
    @DisplayName("isOnPage compares the tail of the path below /api")
    void isOnPageMatchesTheTailOfThePath() {
        final PathHelper pathHelper = pathHelperFor(CONTEXT_PATH + "/api/welcome");

        assertThat(pathHelper.isOnPage("/welcome")).isTrue();
        assertThat(pathHelper.isOnPage("welcome")).isTrue();
        assertThat(pathHelper.isOnPage("/basic/index")).isFalse();
    }

    @Test
    @DisplayName("isOnSubpage matches anywhere inside the path below /api")
    void isOnSubpageMatchesAnywhereInThePath() {
        final PathHelper pathHelper = pathHelperFor(CONTEXT_PATH + "/api/basic/scopes/mixed");

        assertThat(pathHelper.isOnSubpage("/basic")).isTrue();
        assertThat(pathHelper.isOnSubpage("/scopes")).isTrue();
        assertThat(pathHelper.isOnSubpage("/advanced")).isFalse();
    }

    @Test
    @DisplayName("a URI outside /api falls back to the path below the context root")
    void pathInfoFallsBackToTheContextRelativePath() {
        final PathHelper pathHelper = pathHelperFor(CONTEXT_PATH + "/resources/css/style.css");

        assertThat(pathHelper.isOnPage("/resources/css/style.css")).isTrue();
        assertThat(pathHelper.isOnSubpage("/resources")).isTrue();
        assertThat(pathHelper.isOnSubpage("/api")).isFalse();
    }
}
