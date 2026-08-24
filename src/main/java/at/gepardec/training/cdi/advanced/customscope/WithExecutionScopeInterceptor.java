package at.gepardec.training.cdi.advanced.customscope;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Spring AOP counterpart of the CDI interceptor that was bound by {@link WithExecutionScope}.
 * <p>
 * The CDI interceptor was enabled in {@code META-INF/beans.xml}, therefore the aspect is a {@code @Component} and is
 * active. The pointcut reproduces the interceptor binding: class level bindings are matched by {@code @within}, method
 * level bindings by {@code @annotation}.
 */
@Aspect
@Component
public class WithExecutionScopeInterceptor {

    @Autowired
    private ExecutionContextController executionContextController;

    @Around("@annotation(at.gepardec.training.cdi.advanced.customscope.WithExecutionScope)"
            + " || @within(at.gepardec.training.cdi.advanced.customscope.WithExecutionScope)")
    public Object invoke(final ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            executionContextController.activate();
            return joinPoint.proceed();
        } finally {
            executionContextController.deactivate();
        }
    }
}
