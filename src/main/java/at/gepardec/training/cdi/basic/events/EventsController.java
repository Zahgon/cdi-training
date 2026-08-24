package at.gepardec.training.cdi.basic.events;

import at.gepardec.training.cdi.MvcApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/basic/events")
@Controller
@RequestScope
public class EventsController {

    @GetMapping({"", "/"})
    public String get() {
        return "basic/events";
    }
}
