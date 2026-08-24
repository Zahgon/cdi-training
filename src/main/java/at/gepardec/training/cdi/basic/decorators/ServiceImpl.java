package at.gepardec.training.cdi.basic.decorators;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceImpl implements ServiceApi {

    @Autowired
    private Logger log;

    @Override
    public void decorated() {
        log.info("decorated. id=" + this.hashCode());
    }

    @Override
    public void nonDecorated() {
        log.info("nonDecorated. id=" + this.hashCode());
    }
}
