package at.gepardec.training.cdi.advanced.registrar;

import at.gepardec.training.cdi.Models;
import at.gepardec.training.cdi.MvcApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

/**
 * this controller fires the asynchronous events which results we need to capture.
 */
@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/advanced/registrar")
@RequestScope
@Controller
public class RegistrarController {

    @Autowired
    private EventResultRegistrar eventResultRegistrar;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private Models model;

    @GetMapping({"", "/"})
    public String get() {
        fillData();

        return "advanced/registrar";
    }

    @GetMapping("/clear")
    public String clear() {
        eventResultRegistrar.clear();

        return get();
    }

    @GetMapping("/fire/{fail}")
    public String fire(@PathVariable(name = "fail", required = false) String fail) {
        publisher.publishEvent(EventData.of(UUID.randomUUID().toString(), "failedEvent".equals(fail)));

        return get();
    }

    private void fillData() {
        model.put("failedEvents", eventResultRegistrar.failedEvents());
        model.put("successEvents", eventResultRegistrar.successEvents());
    }
}
