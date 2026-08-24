package at.gepardec.training.cdi.basic.interceptors;

import at.gepardec.training.cdi.Util;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseInterceptor {

    @Autowired
    protected Logger logger;

    protected Object logAndProceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        String interceptedMethodInfo = Util.nameWithoutProxy(joinPoint.getTarget()) + "." + joinPoint.getSignature().getName() + "(...)";
        logger.info("start  -> " + interceptedMethodInfo);
        final Object result = joinPoint.proceed();
        logger.info("end  -> " + interceptedMethodInfo);
        return result;
    }
}
