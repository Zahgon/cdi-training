package at.gepardec.training.cdi.advanced.instance;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Default
@Primary
@Component
@RequestScope
public class BeanChild implements BeanInterfaceChild {
}
