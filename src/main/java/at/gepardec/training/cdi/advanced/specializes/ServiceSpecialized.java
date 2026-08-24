package at.gepardec.training.cdi.advanced.specializes;

import at.gepardec.training.cdi.Util;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * This bean is a specialized implementation of a actual bean, so we need to inherit the bean we specialize.
 */
@Component
@RequestScope
@Primary
public class ServiceSpecialized extends ServiceOriginal {

    @Override
    public String execute() {
        return Util.nameWithInstanceId(this);
    }
}
