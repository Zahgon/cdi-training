package at.gepardec.training.cdi.advanced.specializes;

import at.gepardec.training.cdi.Models;
import at.gepardec.training.cdi.MvcApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/advanced/specializes")
@RequestScope
@Controller
public class SpecializesController {

    /**
     * No need to know which implementation we use during compile time
     */
    @Autowired
    private Service service;

    /**
     * Still we get {@link ServiceSpecialized} because we inherit from it, this is who the overwrite works
     */
    @Autowired
    private ServiceOriginal serviceOriginal;

    @Autowired
    private Models model;

    @GetMapping({"", "/"})
    public String get() {
        model.put("result", service.execute());
        model.put("resultOriginal", serviceOriginal.execute());
        return "advanced/specializes";
    }
}
