package at.gepardec.training.cdi.basic.decorators;

import at.gepardec.training.cdi.MvcApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/basic/decorators")
@RequestScope
@Controller
public class DecoratorsController {

    @Autowired
    private ServiceApi service;

    @GetMapping({"", "/"})
    public String get() {
        service.decorated();
        service.nonDecorated();
        return "basic/decorators";
    }
}
