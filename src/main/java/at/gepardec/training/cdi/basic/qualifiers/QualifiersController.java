package at.gepardec.training.cdi.basic.qualifiers;

import at.gepardec.training.cdi.Models;
import at.gepardec.training.cdi.MvcApplication;
import at.gepardec.training.cdi.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Implement a CDI qualifier ensure tha the injected field 'rectangle' is a RectangleShape instance.
 * Don't change the type of the fields.
 */
@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/basic/qualifiers")
@RequestScope
@Controller
public class QualifiersController {

    @Autowired
    private Models model;

    @Autowired
    private Shape circle;

    @Autowired
    private Shape rectangle;

    @GetMapping({"", "/"})
    public String get() {
        model.put("circle", Util.nameWithoutProxy(circle));
        model.put("rectangle", Util.nameWithoutProxy(rectangle));

        return "basic/qualifiers";
    }
}
