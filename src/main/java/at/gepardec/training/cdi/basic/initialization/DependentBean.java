package at.gepardec.training.cdi.basic.initialization;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Not a normal scoped bean, but callbacks are still invoked.
 * Call logInit(); for initialization
 * Call logDestroy(); for destruction
 */
@Component("initializationDependentBean")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DependentBean extends BaseBean {

}
