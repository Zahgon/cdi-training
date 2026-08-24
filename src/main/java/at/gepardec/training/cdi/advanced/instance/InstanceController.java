package at.gepardec.training.cdi.advanced.instance;

import at.gepardec.training.cdi.Models;
import at.gepardec.training.cdi.MvcApplication;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RequestScope
@Controller
@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/advanced/instance")
public class InstanceController {

    @Autowired
    private Models models;

    /**
     * The Spring counterpart of the CDI {@code BeanManager}, used for the programmatic lookup.
     */
    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    /**
     * {@code @Default} stands in for the qualifier CDI applies implicitly to an unqualified
     * injection point. Without it this provider streams {@code SecondBeanChild} too and the page
     * shows the same two beans for {@code @Inject} as for {@code @Inject @Any}.
     */
    @Autowired
    @Default
    private ObjectProvider<BeanInterfaceChild> beanInterfaceChildrenInstanceDefault;

    @Autowired
    private ObjectProvider<BeanInterfaceChild> beanInterfaceChildrenInstanceAny;

    @Autowired
    @Second
    private ObjectProvider<BeanInterfaceChild> beanInterfaceChildrenInstanceSecond;

    @Autowired
    private ObjectProvider<BeanInterfaceRoot> beanInterfaceRootInstance;

    @Autowired
    private ObjectProvider<BeanParent> beanParentInstance;

    @GetMapping({"", "/"})
    public String get() {
        final Map<String, List<String>> data = new LinkedHashMap<>();
        fillInForTypeAndInstance(data, "@Inject @Any", BeanInterfaceRoot.class, beanInterfaceRootInstance);
        fillInForTypeAndInstance(data, "@Inject @Any", BeanParent.class, beanParentInstance);
        fillInForTypeAndInstance(data, "@Inject", BeanInterfaceChild.class, beanInterfaceChildrenInstanceDefault);
        fillInForTypeAndInstance(data, "@Inject @Any", BeanInterfaceChild.class, beanInterfaceChildrenInstanceAny);
        fillInForTypeAndInstance(data, "@Inject @Second", BeanInterfaceChild.class, beanInterfaceChildrenInstanceSecond);
        fillInProgrammaticLookup(data);
        models.put("data", data);
        return "advanced/instances";
    }

    private void fillInProgrammaticLookup(Map<String, List<String>> data) {
        data.put("instance.select(new AnnotationLiteral<Default>(){})", List.of(beanInterfaceChildrenInstanceAny.getObject().getName()));
        data.put("instance.select(new SecondLiteral())", List.of(new SecondLiteral().select(beanFactory).getName()));
    }

    private <T extends BeanInterfaceRoot> void fillInForTypeAndInstance(Map<String, List<String>> data, final String annotations, final Class<T> clazz, final ObjectProvider<T> instance) {
        final List<String> names = new LinkedList<>();
        instance.stream().forEach(bean -> names.add(bean.getName()));
        data.put(annotations + " Instance<" + clazz.getSimpleName() + ">", names);
    }
}
