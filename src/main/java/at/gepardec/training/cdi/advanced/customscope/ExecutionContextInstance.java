package at.gepardec.training.cdi.advanced.customscope;

/**
 * This model holds the instance and the framework related destruction hook, so we can properly destroy the bean wrapping
 * the acual instance.
 * <p>
 * With CDI the {@code Contextual} together with its {@code CreationalContext} was needed to destroy a contextual
 * instance. Spring instead hands the scope a {@link Runnable} through
 * {@link org.springframework.beans.factory.config.Scope#registerDestructionCallback(String, Runnable)}, so this holder
 * keeps that callback. Both fields are mutable on purpose: Spring registers the destruction callback from inside the
 * {@code ObjectFactory}, that is while the instance is still being created and before the factory has returned it.
 */
public class ExecutionContextInstance {

    private Object instance;

    private Runnable destructionCallback;

    public void destroy() {
        if (destructionCallback != null) {
            destructionCallback.run();
        }
    }

    public Object getInstance() {
        return instance;
    }

    void setInstance(Object instance) {
        this.instance = instance;
    }

    void setDestructionCallback(Runnable destructionCallback) {
        this.destructionCallback = destructionCallback;
    }
}
