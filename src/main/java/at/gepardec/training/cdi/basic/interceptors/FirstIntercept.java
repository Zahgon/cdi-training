package at.gepardec.training.cdi.basic.interceptors;

import java.lang.annotation.*;

/**
 * Spring AOP has no interceptor bindings: the binding is expressed by the pointcut of the
 * aspect instead, so {@code @InterceptorBinding} is dropped and this stays a plain runtime
 * annotation that {@link FirstInterceptor} matches on.
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FirstIntercept {
}
