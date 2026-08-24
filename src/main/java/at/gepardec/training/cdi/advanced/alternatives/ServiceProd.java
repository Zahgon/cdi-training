package at.gepardec.training.cdi.advanced.alternatives;

import at.gepardec.training.cdi.Util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * That is the default implementation is no alternative is activated.
 * With alternative, this bean is still available in the CDI container!!
 */
@Component
@RequestScope
public class ServiceProd implements Service {

    @Override
    public String execute() {
        return Util.nameWithInstanceId(this);
    }
}
