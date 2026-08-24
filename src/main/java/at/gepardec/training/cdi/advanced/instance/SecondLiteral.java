package at.gepardec.training.cdi.advanced.instance;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;

/**
 * The Spring counterpart of the CDI {@code AnnotationLiteral} for the {@link Second} qualifier.
 * <p>
 * CDI selects a bean by handing an annotation instance to {@code Instance#select}. Spring has no
 * annotation instances in its lookup API, a programmatic lookup selects by qualifier string
 * instead. This class keeps that string and the lookup itself in one place.
 */
public class SecondLiteral {

    /**
     * The name the {@link Second} qualified bean is registered under.
     */
    public static final String QUALIFIER = "second";

    public BeanInterfaceChild select(BeanFactory beanFactory) {
        return BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, BeanInterfaceChild.class, QUALIFIER);
    }
}
