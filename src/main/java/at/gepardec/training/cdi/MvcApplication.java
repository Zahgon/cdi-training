package at.gepardec.training.cdi;

/**
 * Holds the application path all controllers are served under.
 * <p>
 * Formerly a JAX-RS {@code jakarta.ws.rs.core.Application} annotated with
 * {@code @ApplicationPath}. With Spring MVC the prefix is part of every
 * controller's request mapping instead.
 */
public final class MvcApplication {

    public static final String REST_APPLICATION_PATH = "/api";

    private MvcApplication() {
    }
}
