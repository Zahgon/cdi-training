package at.gepardec.training.cdi.advanced.startupevent;

import at.gepardec.training.cdi.MvcApplication;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/advanced/startup-event")
@RequestScope
@Controller
public class StartupEventController {

    @GetMapping({"", "/"})
    public String get() {
        return "advanced/startup-event";
    }
}
