package at.gepardec.training.cdi.basic.scopes;

import at.gepardec.training.cdi.Models;
import at.gepardec.training.cdi.MvcApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Fix the scopes of the injected beans, as well as the scopes of beans they inject
 */
@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/basic/scopes/mixed")
@RequestScope
@Controller
public class ScopesMixedController {

    @Autowired
    private Models model;

    @Autowired
    private MixedApplicationBean mixedApplicationBean;

    @GetMapping({"", "/"})
    public String advanced() {
        model.put("tabTitle", "Mixed Scopes");
        model.put("requestValue", mixedApplicationBean.scopeMixSession().scopeMixRequest().incrementAndGet());
        model.put("sessionValue", mixedApplicationBean.scopeMixSession().incrementAndGet());
        model.put("applicationValue", mixedApplicationBean.incrementAndGet());

        return "basic/scopes-mixed";
    }
}
