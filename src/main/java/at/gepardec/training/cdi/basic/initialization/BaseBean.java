package at.gepardec.training.cdi.basic.initialization;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * Don't do anything here!
 */
public abstract class BaseBean implements Serializable {

    @Autowired
    private Logger log;

    public void logInit() {
        log.info(this.getClass().getSimpleName() + " got initialized");
    }

    public void logDestroy() {
        log.info(this.getClass().getSimpleName() + " got destroyed");
    }
}
