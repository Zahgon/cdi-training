package at.gepardec.training.cdi.advanced.customscope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The execution-context implementation which handles the instance management.
 * <p>
 * It implements Spring's {@link org.springframework.beans.factory.config.Scope} SPI instead of the CDI
 * {@code AlterableContext}. Spring identifies a scoped instance by bean name and not by {@code Contextual}, therefore
 * the ThreadLocal map is keyed by the bean name; creation is delegated to the {@link ObjectFactory} the bean factory
 * passes in, which is the counterpart of {@code Contextual#create(CreationalContext)}.
 * <p>
 * The instance is created directly by {@link ExecutionContextExtension} and is deliberately not a Spring bean itself,
 * which is what the CDI {@code @Vetoed} expressed.
 */
public class ExecutionContextImpl implements ExecutionContext {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutionContextImpl.class);

    /**
     * An execution-context is bound to the current Thread whereby ThreadLocal manages the bean instances.
     */
    private static ThreadLocal<Map<String, ExecutionContextInstance>> INSTANCES = new ThreadLocal<>();

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        final var instancesOfThread = INSTANCES.get();
        if (instancesOfThread == null) {
            // The Spring counterpart of the CDI ContextNotActiveException
            throw new IllegalStateException("No active context for current Thread");
        }
        ExecutionContextInstance instance = instancesOfThread.get(name);
        if (instance == null && objectFactory != null) {
            // The holder is published before the instance exists, because Spring calls back into
            // registerDestructionCallback(String, Runnable) from within objectFactory.getObject().
            instance = new ExecutionContextInstance();
            instancesOfThread.put(name, instance);
            try {
                instance.setInstance(objectFactory.getObject());
            } catch (RuntimeException | Error e) {
                instancesOfThread.remove(name);
                throw e;
            }
        }

        return (instance != null) ? instance.getInstance() : null;
    }

    @Override
    public Object remove(String name) {
        var instancesOfThread = INSTANCES.get();
        if (instancesOfThread == null) {
            return null;
        }
        var instance = instancesOfThread.remove(name);

        return (instance != null) ? instance.getInstance() : null;
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        var instancesOfThread = INSTANCES.get();
        if (instancesOfThread != null) {
            var instance = instancesOfThread.get(name);
            if (instance != null) {
                instance.setDestructionCallback(callback);
            }
        }
    }

    /**
     * The execution-context has no contextual objects to expose, unlike the request scope which resolves the request
     * and the session here.
     */
    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    /**
     * The execution-context is bound to a Thread, so the Thread identifies the conversation.
     */
    @Override
    public String getConversationId() {
        return isActive() ? Thread.currentThread().getName() : null;
    }

    @Override
    public boolean isActive() {
        return INSTANCES.get() != null;
    }

    public void destroy(String name) {
        var instancesOfThread = INSTANCES.get();
        if (instancesOfThread != null) {
            var instance = instancesOfThread.get(name);
            if (instance != null) {
                try {
                    instance.destroy();
                } catch (Exception e) {
                    LOG.warn("Could not destroy the contextual instance. Error: " + e.getMessage());
                }
            }
        }
    }

    public void activate() {
        INSTANCES.set(Collections.synchronizedMap(new HashMap<>(50, 1)));
    }

    public void deactivate() {
        Optional.ofNullable(INSTANCES.get()).ifPresent(map -> map.keySet().forEach(this::destroy));
        INSTANCES.remove();
    }
}
