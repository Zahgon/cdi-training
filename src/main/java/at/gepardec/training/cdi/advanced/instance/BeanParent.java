package at.gepardec.training.cdi.advanced.instance;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class BeanParent implements BeanInterfaceRoot {
}
