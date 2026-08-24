package at.gepardec.training.cdi.advanced.alternatives;

import at.gepardec.training.cdi.Models;
import at.gepardec.training.cdi.MvcApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/advanced/alternatives")
@RequestScope
@Controller
public class AlternativesController {

    /**
     * No need to know which implementation we use during compile time
     */
    @Autowired
    private Service service;

    @Autowired
    private ServiceProd serviceProd;

    @Autowired
    private Models model;

    @GetMapping({"", "/"})
    public String get() {
        model.put("result", service.execute());
        model.put("resultOriginal", serviceProd.execute());
        return "advanced/alternatives";
    }
}
