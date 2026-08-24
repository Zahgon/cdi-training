package at.gepardec.training.cdi;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Copies everything a bean wrote into the request scoped {@link Models} instance into the
 * model of the view that is about to be rendered.
 */
@Component
public class ModelsMergingInterceptor implements HandlerInterceptor {

    private final ObjectProvider<Models> models;

    public ModelsMergingInterceptor(ObjectProvider<Models> models) {
        this.models = models;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView == null) {
            return;
        }
        final Models requestModels = models.getIfAvailable();
        if (requestModels != null) {
            modelAndView.addAllObjects(requestModels.asMap());
        }
    }
}
