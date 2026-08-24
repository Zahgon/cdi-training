package at.gepardec.training.cdi.basic.initialization;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Any scope but @Dependent is a normal scope.
 * Call logInit(); for initialization
 * Call logDestroy(); for destruction
 */
@Component
@RequestScope
public class NormalScopedBean extends BaseBean {

}
