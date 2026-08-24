package at.gepardec.training.cdi.basic.inject;

import at.gepardec.training.cdi.MvcApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Here two things are wrong. Are you up to find them?
 */
@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/basic/inject/constructor")
@RequestScope
@Controller
public class InjectConstructorController {

    private InjectModel model;

    /**
     * Try to remove me and restart your server and see what happens
     */
    public InjectConstructorController() {
    }

    public InjectConstructorController(InjectModel model) {
        this.model = model;
    }

    @GetMapping({"", "/"})
    public String get() {
        model.setForView("Your name goes here aas well :)");
        return "basic/inject-constructor";
    }
}
