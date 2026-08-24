package at.gepardec.training.cdi.advanced.lookupfactory;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * The scope is important because of the @Dependent scoped beans we have to manage.
 * This bean lives as long as the owning bean, so take care its in the proper scope.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ServiceFactory implements DisposableBean {

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    private final List<Service> dependentServiceInstances = new LinkedList<>();

    /**
     * Here we cleanup the dependent scoped beans to avoid a memory leak.
     */
    @Override
    public void destroy() {
        dependentServiceInstances.forEach(beanFactory::destroyBean);
    }

    /**
     * This is the factory method which resolves the bean and takes care about dependent instance handling
     */
    public Service forType(String type) {
        final String literal = Objects.requireNonNull(type, "Type must be set");
        final Service service = BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, Service.class, literal);
        if (isDependentScoped(literal)) {
            dependentServiceInstances.add(service);
        }
        return service;
    }

    /**
     * Here we get the bean definition from the CDI container and check what the scope of the implementation class is.
     */
    private boolean isDependentScoped(String literal) {
        final List<String> beans = Arrays.stream(beanFactory.getBeanNamesForType(Service.class))
                .filter(literal::equals)
                .toList();
        if (beans.size() == 1) {
            return beanFactory.getBeanDefinition(beans.get(0)).isPrototype();
        } else if (!beans.isEmpty()) {
            throw new IllegalStateException("Found multiple beans for service class and annotation literal: " + literal);
        } else {
            throw new IllegalArgumentException("No beans found for Service interface and annotation literal: " + literal);
        }
    }
}
