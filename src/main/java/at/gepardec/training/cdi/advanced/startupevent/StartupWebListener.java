package at.gepardec.training.cdi.advanced.startupevent;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * When the servlet container is up then the CDI container is up as well
 */
// Startup listener to notify parts of the application that the application has been started
@Component
public class StartupWebListener {

    @Autowired
    private Logger log;

    @Autowired
    private ApplicationEventPublisher startupEvent;

    /**
     * Fire a synchronous CDI event to notify parts of the application that the application has started.
     * There is a WELD specific event (if you are using WELD) but this is the approach without third party dependencies
     */
    @EventListener(ApplicationReadyEvent.class)
    public void contextInitialized() {
        // Creating the event
        final StartupEvent event = new StartupEvent();
        log.info("Notifying Observers about startup");
        // Firing the event
        startupEvent.publishEvent(event);
        // Log the observers who have observed this event
        log.info("Notified Observers: " + String.join(", ", event.observerIds()));
    }
}
