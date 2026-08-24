package at.gepardec.training.cdi.advanced.lookupfactory;

import at.gepardec.training.cdi.Util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * The bean name is the discriminator {@link ServiceFactory} resolves by: Spring matches a
 * qualifier string against the bean name, but it cannot derive one from {@link TypedService#value()}
 * because its own {@code @Qualifier} meta annotation carries no value.
 */
@Component("A")
@RequestScope
@TypedService("A")
public class ServiceA implements Service {
    @Override
    public String execute() {
        return Util.nameWithInstanceId(this);
    }
}
