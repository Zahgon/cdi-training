package at.gepardec.training.cdi.advanced.concurrency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This is bean is executed on another Thread and cannot have any dependency to beans outside the following scopes:
 * <ol>
 *     <li>@ApplicationScoped // Exists always</li>
 *     <li>@Dependent         // Exists for the depending bean only</li>
 * </ol>
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Service {

    /**
     * Nevertheless that this is actually request scoped, it's actually not, because there is no proxy causing problems
     */
    @Autowired
    private Context config;

    public String execute() {
        return Thread.currentThread().getId() + " (" + config.getRequestUri() + ")";
    }
}
