package at.gepardec.training.cdi.advanced.alternatives;

import at.gepardec.training.cdi.Util;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * This bean is the alternative implementation which is not enabled unless added to the beans.xml.
 */
@Component
@RequestScope
@Primary
public class ServiceDev implements Service {

    @Override
    public String execute() {
        return Util.nameWithInstanceId(this);
    }
}
