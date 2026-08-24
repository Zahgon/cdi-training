package at.gepardec.training.cdi.advanced.customscope;

import at.gepardec.training.cdi.Models;
import at.gepardec.training.cdi.MvcApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequestScope
@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/advanced/customscope")
@Controller
public class CustomscopeController {

    private static final int ITERATION_COUNT_WITHIN_SCOPE = 3;

    @Autowired
    private Models models;

    @Autowired
    private WithExecutionScopeService service;

    @GetMapping({"", "/"})
    public String get(){
        models.put("data", buildModelData());
        return "advanced/customscope";
    }

    private Map<String, List<String>> buildModelData() {
        Map<String, List<String>> idsPerExecution = new LinkedHashMap<>();

        idsPerExecution.put("Execution 1", service.executeWithinScope(ITERATION_COUNT_WITHIN_SCOPE));
        idsPerExecution.put("Execution 2", service.executeWithinScope(ITERATION_COUNT_WITHIN_SCOPE));

        return idsPerExecution;
    }
}
