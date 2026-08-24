package at.gepardec.training.cdi.advanced.registrar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Just firing an event and forgetting it mostly not possible.
 * The registrar is the component which holds the results and can be accessed by other
 * CDI beans to evaluate the captured results.
 */
@Component("registrarEventObserver")
public class EventObserver {

    @Autowired
    private EventResultRegistrar eventResultRegistrar;

    /**
     * The method is public because {@code @Async} is applied by a proxy, which can only advise
     * methods that are visible to it.
     */
    @Async
    @EventListener
    public void observe(EventData event) {
        if (event.fail) {
            eventResultRegistrar.registerFailedEvent(event.id);
        } else {
            try {
                Thread.sleep(3000);
                eventResultRegistrar.registerSuccessEvent(event.id);
            } catch (InterruptedException e) {
                eventResultRegistrar.registerFailedEvent(event.id);
            }
        }
    }
}
