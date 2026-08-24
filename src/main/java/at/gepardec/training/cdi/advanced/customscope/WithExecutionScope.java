package at.gepardec.training.cdi.advanced.customscope;

import java.lang.annotation.*;

/**
 * Marks a type or a method whose invocation shall run inside an active execution-context.
 * <p>
 * Spring has no interceptor bindings, so the CDI {@code @InterceptorBinding} meta-annotation is dropped and the
 * annotation is bound to the advice through the pointcut of {@link WithExecutionScopeInterceptor} instead.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface WithExecutionScope {
}
