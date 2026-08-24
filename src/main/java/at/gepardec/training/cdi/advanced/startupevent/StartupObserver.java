package at.gepardec.training.cdi.advanced.startupevent;

import at.gepardec.training.cdi.Util;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupObserver {

    @Autowired
    private Logger log;

    /**
     * The listener is synchronous, it carries no {@code @Async}, so the observer has registered
     * itself by the time the publisher logs the observers.
     * <p>
     * {@code Util.nameWithoutProxy(this)} instead of {@code getClass().getSimpleName()} so the
     * name stays {@code StartupObserver} even if this bean ever ends up behind a CGLIB proxy.
     */
    @EventListener
    public void observeStartup(StartupEvent event) {
        event.add(Util.nameWithoutProxy(this));
        log.info("Startup observed by " + Util.nameWithoutProxy(this));
    }
}
