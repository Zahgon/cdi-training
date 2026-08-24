package at.gepardec.training.cdi.advanced.instance;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Inherited
public @interface Second {
}
