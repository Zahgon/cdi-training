package at.gepardec.training.cdi.basic.events;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component("basicEventObserver")
public class EventObserver {

    @Autowired
    Logger logger;

    /**
     * The method which observes a event of type String
     *
     * @param messageEvent the event this method observes
     * @throws Exception If the sleep fails
     */
    @Async
    @EventListener
    void observe(String messageEvent) throws Exception {
        Thread.sleep(3000);
        logger.info("Message received. Message: '{}'", messageEvent);
    }
}
