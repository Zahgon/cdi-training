package at.gepardec.training.cdi.advanced.customscope;

/**
 * The bean for controlling the execution-context lifecycle.
 */
public class ExecutionContextControllerImpl implements ExecutionContextController {

    private final ExecutionContext context;

    public ExecutionContextControllerImpl(ExecutionContext context) {
        this.context = context;
    }

    @Override
    public void activate() {
        if (!context.isActive()) {
            context.activate();
        }
    }

    @Override
    public void deactivate() {
        if (context.isActive()) {
            context.deactivate();
        }
    }
}
