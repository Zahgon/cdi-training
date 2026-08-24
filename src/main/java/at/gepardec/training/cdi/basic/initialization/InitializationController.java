package at.gepardec.training.cdi.basic.initialization;

import at.gepardec.training.cdi.MvcApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Implement a lifecycle callback methods in the injected beans.
 */
@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/basic/initialization")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Controller
public class InitializationController {

    @Autowired
    private NormalScopedBean requestBean;

    @Autowired
    private DependentBean dependentBean;

    /**
     * Remember that CDI beans are initialized lazily, maybe you need to do something else in this method.
     */
    @GetMapping({"", "/"})
    public String get() {
        return "basic/initialization";
    }
}
