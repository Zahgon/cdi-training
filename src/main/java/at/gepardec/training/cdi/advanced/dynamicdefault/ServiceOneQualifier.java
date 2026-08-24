package at.gepardec.training.cdi.advanced.dynamicdefault;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

@Inherited
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ServiceOneQualifier {
}
