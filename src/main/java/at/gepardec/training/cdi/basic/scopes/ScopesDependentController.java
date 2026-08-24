package at.gepardec.training.cdi.basic.scopes;

import at.gepardec.training.cdi.Models;
import at.gepardec.training.cdi.MvcApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/basic/scopes/dependent")
@RequestScope
@Controller
public class ScopesDependentController {

    @Autowired
    private Models model;

    @Autowired
    private RequestBean requestBean;

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private ApplicationBean applicationBean;

    @GetMapping({"", "/"})
    public String dependent() {
        model.put("dependentBeanRequestScopedValue", requestBean.dependentBean().incrementAndGet());
        model.put("dependentBeanSessionScopedValue", sessionBean.dependentBean().incrementAndGet());
        model.put("dependentBeanApplicationScopedValue", applicationBean.dependentBean().incrementAndGet());

        return "basic/scopes-dependent";
    }
}
