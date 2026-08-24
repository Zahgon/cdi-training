package at.gepardec.training.cdi.basic.scopes;

import at.gepardec.training.cdi.Models;
import at.gepardec.training.cdi.MvcApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Fix the scopes of the injected beans
 */
@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/basic/scopes/basic")
@RequestScope
@Controller
public class ScopesBasicController {

    @Autowired
    private Models model;

    @Autowired
    private RequestBean requestBean;

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private ApplicationBean applicationBean;

    @GetMapping({"", "/"})
    public String getBasic() {
        model.put("tabTitle", "Scopes Basic");
        model.put("requestValue", requestBean.incrementAndGet());
        model.put("sessionValue", sessionBean.incrementAndGet());
        model.put("applicationValue", applicationBean.incrementAndGet());

        return "basic/scopes-basic";
    }

}
