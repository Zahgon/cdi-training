package at.gepardec.training.cdi.advanced.customscope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The annotation activates the Spring AOP aspect which handles the context activation/deactivation.
 */
@WithExecutionScope
@Component
@RequestScope
public class WithExecutionScopeService {

    /**
     * The programmatic lookup replacing {@code CDI.current()}. It has to stay a lookup instead of an injected field,
     * because an injected field would be resolved once when this request scoped bean is created, which happens outside
     * of any active execution-context.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * All code within this method is executed within the context and all beans accessed here are the same instance for the current
     * invocation and will be new instances on another invocation within the same request.
     */
    public List<String> executeWithinScope(int count) {
        // In a loop retrieve the Bean multiple times to see, that it is always the same one within this method
        return IntStream.range(0, count).mapToObj(i -> applicationContext.getBean(IdService.class).toString()).collect(Collectors.toList());
    }
}
