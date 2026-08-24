package at.gepardec.training.cdi.basic.producers;

import at.gepardec.training.cdi.Models;
import at.gepardec.training.cdi.MvcApplication;
import at.gepardec.training.cdi.Util;
import at.gepardec.training.cdi.basic.qualifiers.Circle;
import at.gepardec.training.cdi.basic.qualifiers.Rectangle;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/basic/producers")
@RequestScope
@Controller
public class ProducersController {

    @Autowired
    private Models model;

    @Autowired
    @Qualifier("producedString")
    private String producedString;

    @Autowired
    private Logger logger;

    @Autowired
    private ProducedBean producedBean;

    @GetMapping({"", "/"})
    public String get() {
        // Here you put your produced lists in
        model.put("circleList", Util.namesWithInstanceId(List.of()));
        model.put("rectangleList", Util.namesWithInstanceId(List.of()));

        model.put("producedString", producedString);
        model.put("producedBean", Util.nameWithInstanceId(producedBean));

        return "basic/producers";
    }
}
