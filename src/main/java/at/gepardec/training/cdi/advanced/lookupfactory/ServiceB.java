package at.gepardec.training.cdi.advanced.lookupfactory;


import at.gepardec.training.cdi.Util;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The bean name is the discriminator {@link ServiceFactory} resolves by, see {@link ServiceA}.
 */
@Component("B")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@TypedService("B")
public class ServiceB implements Service {

    @Override
    public String execute() {
        return Util.nameWithInstanceId(this);
    }
}
