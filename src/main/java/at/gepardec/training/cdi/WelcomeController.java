package at.gepardec.training.cdi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

@Controller
@RequestScope
@RequestMapping(MvcApplication.REST_APPLICATION_PATH)
public class WelcomeController {

    @GetMapping({"", "/"})
    public String get() {
        return welcome();
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }
}
