package at.gepardec.training.cdi.advanced.specializes;

import at.gepardec.training.cdi.Util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * That is the implementation we specialize
 */
@Component
@RequestScope
public class ServiceOriginal implements Service {

    @Override
    public String execute() {
        return Util.nameWithInstanceId(this);
    }
}
