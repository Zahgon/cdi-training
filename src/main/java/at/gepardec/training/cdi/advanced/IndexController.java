package at.gepardec.training.cdi.advanced;

import at.gepardec.training.cdi.MvcApplication;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/advanced/index")
@RequestScope
@Controller("advancedIndexController")
public class IndexController {

    @GetMapping({"", "/"})
    public String index() {
        return "advanced/index";
    }
}
