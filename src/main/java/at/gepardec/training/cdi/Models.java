package at.gepardec.training.cdi;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Request scoped model holder, the Spring counterpart of {@code jakarta.mvc.Models}.
 * <p>
 * Jakarta MVC injects {@code Models} into arbitrary beans, not just into controllers,
 * which Spring's {@code org.springframework.ui.Model} cannot do because it is a method
 * argument. This bean keeps that capability; {@link ModelsMergingInterceptor} copies the
 * collected attributes into the rendering model of the resolved view.
 */
@Component
@RequestScope
public class Models {

    private final Map<String, Object> attributes = new LinkedHashMap<>();

    public void put(String name, Object value) {
        attributes.put(name, value);
    }

    public Object get(String name) {
        return attributes.get(name);
    }

    public Map<String, Object> asMap() {
        return attributes;
    }
}
