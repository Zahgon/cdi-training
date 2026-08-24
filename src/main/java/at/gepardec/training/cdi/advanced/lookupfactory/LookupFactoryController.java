package at.gepardec.training.cdi.advanced.lookupfactory;

import at.gepardec.training.cdi.Models;
import at.gepardec.training.cdi.MvcApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/advanced/lookup-factory")
@RequestScope
@Controller
public class LookupFactoryController {

    @Autowired
    private Models model;

    @Autowired
    private ServiceFactory serviceFactory;

    @GetMapping("/{type}")
    public String get(@PathVariable(name = "type", required = false) String type) {
        model.put("result", serviceFactory.forType(type).execute());
        return "advanced/lookup-factory";
    }
}
