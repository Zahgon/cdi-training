package at.gepardec.training.cdi.advanced.lookupfactory;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * All implementation classes are qualified with this qualifier.
 * The distinction is declared via the bind attribute.
 */
@Inherited
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface TypedService {

    /**
     * This is the binding attribute which selects the qualified implementation
     */
    String value();
}
