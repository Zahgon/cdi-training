package at.gepardec.training.cdi.basic.scopes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestBean {

    @Autowired
    private DependentBean dependentBean;

    private int value = 0;

    public int incrementAndGet() {
        return ++value;
    }

    public DependentBean dependentBean() {
        return dependentBean;
    }
}
