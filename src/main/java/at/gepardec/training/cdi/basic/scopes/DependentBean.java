package at.gepardec.training.cdi.basic.scopes;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component("scopesDependentBean")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DependentBean implements Serializable {

    private int value = 0;

    public int incrementAndGet() {
        return ++value;
    }
}
