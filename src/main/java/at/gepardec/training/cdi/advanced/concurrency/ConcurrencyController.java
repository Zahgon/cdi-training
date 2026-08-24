package at.gepardec.training.cdi.advanced.concurrency;

import at.gepardec.training.cdi.Models;
import at.gepardec.training.cdi.MvcApplication;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

/**
 * This controller triggers the concurrent execution of the Service bean by a GET request.
 */
@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/advanced/concurrency")
@RequestScope
@Controller
public class ConcurrencyController {

    @Autowired
    private Logger log;

    /**
     * When a GET request arrives here, then the service bean and its dependencies are already injected and fully initialized.
     * Therefore, we have no problems executing it on a different Thread.
     * But all injected CDI beans must be dependent or application scoped.
     */
    @Autowired
    private Service service;

    @Autowired
    private AsyncTaskExecutor executorService;

    @Autowired
    private Models model;

    @GetMapping({"", "/"})
    public String get() throws Exception {
        model.put("concurrentResult", executorService.submit(() -> service.execute()).get());
        model.put("controllerResult", Thread.currentThread().getId());
        return "advanced/concurrency";
    }
}
