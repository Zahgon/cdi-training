package at.gepardec.training.cdi;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This class handles the errors by extracting the error information and returning the error page.
 * <p>
 * Replaces the JAX-RS {@code jakarta.ws.rs.ext.ExceptionMapper} provider.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log;

    private final Models models;

    private final CdiUri cdiUri;

    private final PathHelper pathHelper;

    public GlobalExceptionHandler(Logger log, Models models, CdiUri cdiUri, PathHelper pathHelper) {
        this.log = log;
        this.models = models;
        this.cdiUri = cdiUri;
        this.pathHelper = pathHelper;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView toResponse(Throwable exception, HttpServletRequest request) {
        final ModelAndView view = new ModelAndView("error");
        final String uri = request.getRequestURL().toString();

        models.put("exceptionType", exception.getClass().getName());
        models.put("exceptionMessage", exception.getMessage());
        models.put("uri", uri);
        models.put("stackTrace", ExceptionUtils.getStackTrace(exception));
        log.error("Error occurred on '" + uri + "'", exception);

        // The interceptor merging the request scoped models is not invoked for handled exceptions
        view.addAllObjects(models.asMap());

        // @ModelAttribute methods of a @ControllerAdvice are not applied to the model of an
        // @ExceptionHandler, so the beans the layout template resolves must be added explicitly.
        view.addObject("cdiUri", cdiUri);
        view.addObject("pathHelper", pathHelper);
        return view;
    }
}
