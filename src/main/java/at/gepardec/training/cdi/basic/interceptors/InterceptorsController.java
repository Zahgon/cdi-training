package at.gepardec.training.cdi.basic.interceptors;

import at.gepardec.training.cdi.MvcApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/basic/interceptors")
@RequestScope
@Controller
@SecondIntercept
@FirstIntercept
public class InterceptorsController {

    @GetMapping({"", "/"})
    @SecondIntercept
    public String basic() {
        return "basic/interceptors";
    }
}
