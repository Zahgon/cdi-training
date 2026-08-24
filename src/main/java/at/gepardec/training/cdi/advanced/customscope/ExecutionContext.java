package at.gepardec.training.cdi.advanced.customscope;

import org.springframework.beans.factory.config.Scope;

/**
 * The custom execution-context interface defining all necessary context specific contracts.
 * <p>
 * The CDI {@code AlterableContext} is replaced by Spring's {@link Scope} SPI, the interface a bean factory talks to for
 * every scope that is not built in. Spring's contract has no {@code isActive()} counterpart, so it is declared here
 * because {@link ExecutionContextControllerImpl} guards activation and deactivation with it.
 */
public interface ExecutionContext extends Scope, ExecutionContextController {

    /**
     * @return {@code true} if an execution-context is currently active for the calling Thread
     */
    boolean isActive();
}
