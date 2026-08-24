package at.gepardec.training.cdi.basic.decorators;

import org.slf4j.Logger;

/**
 * The CDI decorator was never applied: it lacked {@code @Decorator} and {@code @Priority},
 * so the container never enabled it and {@link DecoratorsController} kept getting the plain
 * {@link ServiceImpl}.
 * <p>
 * Spring has no decorator mechanism of its own. The equivalent enabling step would be to
 * make this a concrete {@code @Primary} bean that receives the {@link ServiceImpl} as its
 * delegate, so that it wins over the plain implementation at every {@link ServiceApi}
 * injection point. That is deliberately not done here: the class stays abstract and carries
 * no stereotype, so it is never registered and never wraps anything.
 */
public abstract class ServiceDecorator implements ServiceApi {

    private Logger log;

    private ServiceApi delegate;

    @Override
    public void decorated() {
        log.info("decorated before. id=" + this.hashCode());
        delegate.decorated();
        log.info("decorated after. id=" + this.hashCode());
    }
}
