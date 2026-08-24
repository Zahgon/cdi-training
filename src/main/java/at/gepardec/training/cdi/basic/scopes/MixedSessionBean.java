package at.gepardec.training.cdi.basic.scopes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.io.Serializable;

@Component
@RequestScope
public class MixedSessionBean implements Serializable {

    @Autowired
    private MixedRequestBean mixedRequestBean;

    private int value = 0;

    public MixedRequestBean scopeMixRequest() {
        return mixedRequestBean;
    }

    public int incrementAndGet() {
        return ++value;
    }
}


