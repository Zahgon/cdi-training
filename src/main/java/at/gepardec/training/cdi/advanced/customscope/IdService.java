package at.gepardec.training.cdi.advanced.customscope;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * The execution scoped id service which generates a UUID on construction, where the id doesn't change within the execution-scope.
 * <p>
 * CDI assigned the id in a {@code @PostConstruct} callback. The field initialiser is the Spring equivalent used here and
 * keeps the behaviour that matters: a fresh UUID for every instance the context creates.
 * <p>
 * The class must stay non-final and keep its no-arg constructor, otherwise the CGLIB client proxy that
 * {@link ExecutionScoped} asks for cannot subclass it.
 */
@Component
@ExecutionScoped
public class IdService {

    private final UUID id = UUID.randomUUID();

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "IdService{" +
                "id=" + id +
                '}';
    }
}
