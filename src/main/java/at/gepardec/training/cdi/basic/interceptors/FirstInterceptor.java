package at.gepardec.training.cdi.basic.interceptors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

/**
 * The CDI interceptor never fired: it had no {@code @Priority} and was not listed in the
 * {@code <interceptors>} section of {@code beans.xml}, so the container never enabled it.
 * <p>
 * This aspect reproduces that exactly. Adding {@code @Aspect} and {@code @Component} to this
 * class is the Spring equivalent of adding {@code @Priority} and listing the interceptor in
 * {@code beans.xml} - both are deliberately left out, so the advice below is never woven.
 */
@FirstIntercept
public class FirstInterceptor extends BaseInterceptor {

    @Around("@annotation(FirstIntercept) || @within(FirstIntercept)")
    public Object intercept(ProceedingJoinPoint joinPoint) throws Throwable {
        return logAndProceed(joinPoint);
    }
}
