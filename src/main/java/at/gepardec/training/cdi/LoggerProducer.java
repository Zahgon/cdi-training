package at.gepardec.training.cdi;

import java.lang.reflect.Member;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LoggerProducer {

    /**
     * This is a factory method, the Spring counterpart of a CDI producer method.
     * A prototype scope is required, otherwise the injection point would only be
     * evaluated once for the whole context.
     *
     * @param injectionPoint the injection point the logger is created for
     * @return the produced logger
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Logger produceLogger(InjectionPoint injectionPoint) {
        // The field or constructor parameter the logger is injected into
        final Member member = injectionPoint.getMember();
        if (member != null) {
            return LoggerFactory.getLogger(member.getDeclaringClass());
        }
        // In case we cannot determine declaring class
        else {
            return LoggerFactory.getLogger("default");
        }
    }
}
