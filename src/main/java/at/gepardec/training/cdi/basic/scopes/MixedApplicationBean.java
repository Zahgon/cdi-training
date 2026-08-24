package at.gepardec.training.cdi.basic.scopes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MixedApplicationBean implements Serializable {

    @Autowired
    private MixedSessionBean mixedSessionBean;

    private int value = 0;

    public int incrementAndGet() {
        return ++value;
    }

    public MixedSessionBean scopeMixSession() {
        return mixedSessionBean;
    }
}
