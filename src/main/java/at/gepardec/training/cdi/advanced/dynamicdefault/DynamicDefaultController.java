package at.gepardec.training.cdi.advanced.dynamicdefault;

import at.gepardec.training.cdi.Models;
import at.gepardec.training.cdi.MvcApplication;

import org.slf4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/advanced/dynamic-default")
@RequestScope
@Controller
public class DynamicDefaultController {

    @Autowired
    private ServiceProducer serviceProducer;

    /**
     * The provider is resolved after the implementation type has been switched, otherwise the
     * switch would have no effect within the very same request.
     */
    @Autowired
    // Without '@Default' you would actually declare '@Any' which means give me any bean implementation
    private ObjectProvider<Service> service;

    @Autowired
    private Logger log;

    @Autowired
    private Models model;

    @GetMapping({"", "/"})
    public String get() {
        return get(null);
    }

    @GetMapping("/{type}")
    public String get(@PathVariable(name = "type", required = false) String type) {
        if (type != null && !type.isEmpty()) {
            log.info("Switching implementation from '" + serviceProducer.getImplementationType() + "' -> '" + type + "'");
            serviceProducer.setImplementationType(type);
        }
        model.put("result", service.getObject().execute());
        return "advanced/dynamic-default";
    }
}
