package at.gepardec.training.cdi.advanced.instance;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * The bean is registered under the name {@link SecondLiteral#QUALIFIER} so that the programmatic
 * lookup works: Spring matches a qualifier string against the bean name and against the value of a
 * {@code @Qualifier}, and it cannot derive a value from the attribute-less {@link Second}.
 */
@Second
@Component(SecondLiteral.QUALIFIER)
@RequestScope
public class SecondBeanChild implements BeanInterfaceChild {
}
