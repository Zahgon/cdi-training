package at.gepardec.training.cdi;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ModelsMergingInterceptor modelsMergingInterceptor;

    public WebConfig(ModelsMergingInterceptor modelsMergingInterceptor) {
        this.modelsMergingInterceptor = modelsMergingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(modelsMergingInterceptor);
    }

    /**
     * Serves the images and stylesheets that used to live in {@code src/main/webapp/resources}
     * under the very same URL they had on WildFly.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/static/resources/");
    }
}
