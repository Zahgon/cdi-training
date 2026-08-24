package at.gepardec.training.cdi.advanced.dynamicdefault;

import at.gepardec.training.cdi.Util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@ServiceTwoQualifier
public class ServiceTwo implements Service {

    @Override
    public String execute() {
        return Util.nameWithInstanceId(this);
    }
}
