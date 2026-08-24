package at.gepardec.training.cdi.advanced.dynamicdefault;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This producer is responsible for deciding what bean is returned for @Default annotated injection points.
 */
@Component
public class ServiceProducer {

    /**
     * Here we get the A implementation
     */
    @Autowired
    @ServiceOneQualifier
    private Service serviceOne;

    /**
     * Here we get the B implementation
     */
    @Autowired
    @ServiceTwoQualifier
    private Service serviceTwo;

    @Autowired
    private Logger log;

    /**
     * Just for demonstration, the actual parameter should come from a configuration
     */
    private String implementationType = "ServiceTwo";

    /**
     * This is the @Default qualified Service bean implementation.
     */
    public Service createService() {
        switch (implementationType) {
            case "ServiceOne":
                log.info("ServiceOne is now default");
                return serviceOne;
            case "ServiceTwo":
                log.info("ServiceTwo is now default");
                return serviceTwo;
            default:
                throw new IllegalArgumentException("implementationType unknown. type: " + implementationType);
        }
    }

    String getImplementationType() {
        return implementationType;
    }

    void setImplementationType(String implementationType) {
        this.implementationType = implementationType;
    }
}
