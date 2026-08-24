package at.gepardec.training.cdi;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Exposes the beans the layout templates reference by name.
 * <p>
 * With Jakarta Faces they were resolved through {@code @Named} and the EL bean resolver;
 * Thymeleaf resolves names from the model, so they are added to every model here.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    private final CdiUri cdiUri;

    private final PathHelper pathHelper;

    public GlobalModelAttributes(CdiUri cdiUri, PathHelper pathHelper) {
        this.cdiUri = cdiUri;
        this.pathHelper = pathHelper;
    }

    @ModelAttribute("cdiUri")
    public CdiUri cdiUri() {
        return cdiUri;
    }

    @ModelAttribute("pathHelper")
    public PathHelper pathHelper() {
        return pathHelper;
    }
}
