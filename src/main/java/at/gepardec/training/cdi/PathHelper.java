package at.gepardec.training.cdi;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component("pathHelper")
public class PathHelper {

    private final HttpServletRequest servletRequest;

    public PathHelper(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public boolean isOnPage(String page) {
        return pathInfo().endsWith(page);
    }

    public boolean isOnSubpage(String page) {
        return pathInfo().contains(page);
    }

    public String basePath() {
        return servletRequest.getServletContext().getContextPath() + MvcApplication.REST_APPLICATION_PATH;
    }

    public String resourcePath() {
        return servletRequest.getServletContext().getContextPath() + "/resources";
    }

    public String buildPath(String path) {
        if (StringUtils.isBlank(path)) {
            return "#";
        } else if (path.startsWith("#") || path.startsWith("http")) {
            return path;
        }
        return basePath() + path;
    }

    public String buildContextRootedPath(String path) {
        return servletRequest.getServletContext().getContextPath() + path;
    }

    public String buildResourcePath(String path) {
        return resourcePath() + path;
    }

    /**
     * The part of the request URI below the application path.
     * <p>
     * With JAX-RS this was {@code HttpServletRequest#getPathInfo()}, because the JAX-RS
     * servlet was mapped to {@code /api/*}. Spring's DispatcherServlet is mapped to
     * {@code /}, so the same value has to be derived from the request URI.
     * <p>
     * Returning {@code null} for the bare application path is deliberate. A servlet mapped
     * to {@code /api/*} reports null path info for {@code /api} and {@code "/"} only once a
     * trailing slash is present, so upstream {@code GET /cditraining/api} dies with a
     * NullPointerException in {@link #isOnPage(String)}. Returning {@code ""} here would
     * silently repair that upstream bug and turn a 500 into a 200.
     */
    private String pathInfo() {
        final String contextPath = servletRequest.getServletContext().getContextPath();
        final String uri = servletRequest.getRequestURI();
        final String applicationPath = contextPath + MvcApplication.REST_APPLICATION_PATH;
        if (uri.startsWith(applicationPath)) {
            final String pathInfo = uri.substring(applicationPath.length());
            return pathInfo.isEmpty() ? null : pathInfo;
        }
        return uri.substring(contextPath.length());
    }
}
