package at.gepardec.training.cdi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring Boot entry point.
 * <p>
 * Replaces the Jakarta EE deployment model (WAR on WildFly) with an executable jar
 * running an embedded servlet container.
 */
@SpringBootApplication
@EnableAsync
@EnableAspectJAutoProxy
public class CdiTrainingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CdiTrainingApplication.class, args);
    }
}
