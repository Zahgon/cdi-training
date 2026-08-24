package at.gepardec.training.cdi.basic.inject;

import at.gepardec.training.cdi.MvcApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

/**
 * What's going on wrong in this endpoint, so that it fails?
 */
@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/basic/inject/field")
@RequestScope
@Controller
public class InjectFieldController {

    private InjectModel model = new InjectModel();

    @GetMapping({"", "/"})
    public String get() {
        model.setForView("Your name goes here as well :)");
        return "basic/inject-field";
    }
}
