# Developer Guide

This page represents the guide which will help you to setup the project on your local machine and tells you how to contribute to the project.

The project is based on the following listed frameworks.

* `Spring-Boot-3.3.5` (packaged as an executable jar)
* `Spring-MVC` (`spring-boot-starter-web`)
* `Thymeleaf` (`spring-boot-starter-thymeleaf`) as template engine, together with `thymeleaf-layout-dialect-3.3.0`
* `Spring-AOP` (`spring-boot-starter-aop`)
* `Commons-Lang-3.12.0`

The project uses the following client side libraries in form os webjars.

* `MDB-3.9.0` (Based on Bootstrap 5.Final)
* `Fontawesome-5.15.2`

## System Requirements

This project depends on the following software:

1. [OpenJDK-17](https://jdk.java.net/17/)
2. [Maven-3.x.x](https://maven.apache.org/download.cgi)

No application server has to be installed. Spring Boot packages the application as an executable jar which starts an
embedded Tomcat, so `mvn spring-boot:run` or `java -jar target/cdi-training-<version>.jar` is all that is needed to run it.

## How to set up your environment

Install the depending software, check out the github repository and setup the IDE of your choice.
See the repository root README for further details.

## How to provide documentation

All of our documentation is available as Github Pages, which relates to the projects repository.

## How to use Git Branching

We use the simple [Github-Flow](https://guides.github.com/introduction/flow/) because we need no special version handling. 
We name our feature branches `feature/<ticket_id_or_expresive_name>` and merge them via reviewed Merge-Requests.

## How to implement (MVC) Controllers

Controllers are Spring MVC handler classes with the naming scheme `*Controller` e.g. `InjectController`
which provide http based action endpoints and for views to render.

All controller classes need to be annotated with:

1. `@RequestScope` because in MVC we are stateless
2. `@Controller` which makes the class a Spring MVC handler whose methods return view names
3. `@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/...")` which prefixes the contained endpoints with the
   application path `/api` held by `at.gepardec.training.cdi.MvcApplication`

The http endpoint are named either as:

1. The http method used e.g. `@GetMapping get()`
2. The action name e.g. `@GetMapping("/action") action()`

The endpoints return the `String` name of the view to render, written without the `.html` extension.

See the following snippet for an example controller implementation.

```java
import at.gepardec.training.cdi.Models;
import at.gepardec.training.cdi.MvcApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping(MvcApplication.REST_APPLICATION_PATH + "/basic/index")
@RequestScope
@Controller
public class MyController {

    @Autowired
    private Models model;

    @GetMapping({"", "/"})
    public String get() {
        model.put("tabTitle", "Example");

        return "basic/index";
    }

    @GetMapping("/action")
    public String action() {
        // Do something and return the page
        return get();
    }
}
```

`Models` is the projects own request scoped bean holding the model attributes. It is a request scoped map, and
`ModelsMergingInterceptor`, a `HandlerInterceptor` registered in `WebConfig`, merges its content into the `ModelAndView`
of the resolved view before the view is rendered. Because it is an ordinary bean and not a handler method argument like
`org.springframework.ui.Model`, it can be injected into any bean and not only into controllers, which is what
`GlobalExceptionHandler` relies on.

For further learning about Spring MVC controller see the [Spring MVC documentation](https://docs.spring.io/spring-framework/reference/web/webmvc.html).

## How to implement (MVC) Thymeleaf views

The template engine we have chosen is `Thymeleaf` where we only use the templating part of `Thymeleaf` to provide us with common components, so 
that we have consistent views and an easy way to implement views. The rest is actually plain html, css, text and javascript.

The root directory for our views is `src/main/resources/templates` where we organize our views with the following structure.

* `/context`  
The directory which encapsulates the resources and views of a context which could be for instance `basic` or `advanced`.
   - `/index.html`  
   The index page for a context which is the entry point and contains the links to the several views. 
   - `/example-one.html`  
   The view for the example one. 

A controller names its view without the extension, e.g. `basic/example-one`, because `spring.thymeleaf.prefix` and
`spring.thymeleaf.suffix` in `src/main/resources/application.properties` resolve it to
`classpath:/templates/basic/example-one.html`.

Every view is decorated by the master template `layout/layout.html` via the `thymeleaf-layout-dialect`. The view declares
`layout:decorate="~{layout/layout}"` on its `<html>` element and fills the fragments the layout defines, which are
`title`, `body` and the optional `bottom` inserted at the end of the page.

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/layout}">

<head>
    <title layout:fragment="title">
        Example One
    </title>
</head>

<body>
<div layout:fragment="body">
    <section>
        ...
    </section>
</div>
</body>
</html>
```

See [section](#view-components) for a list of available view components for building GUIs.

## How to provide static resources

Static resources are provided from the classpath below `src/main/resources/static` and are served by the resource handler
of `WebConfig`, which maps `/resources/**` to `classpath:/static/resources/`. They are referenced via normal links,
no template engine involved, either with a link expression such as `th:href="@{/resources/css/style.css}"` or via
`pathHelper.buildResourcePath('/img/cdi-logo.png')`, both of which prepend the context path `/cditraining`.

The webjars are served by Spring Boot under `/webjars/**`, for instance `th:href="@{/webjars/mdb-ui-kit/3.9.0/css/mdb.min.css}"`.

The main thing to consider is that `MvcApplication.REST_APPLICATION_PATH` must not define the root path, because the
controller mappings would then collide with the static resource urls.

The static resource are organized in the root directory `src/main/resources/static/resources` and with the following structure.

* `/<framework>-<version>`   
The directory for a framework such as `mdb` which also defines the version.  
If a webjar of the used framework exists, then the webjar should be used.
* `/css`   
The directory for all the applications stylings.
* `/img`   
The directory for all the applications images.
   * `/<context>`  
   The directory holding the images for a context
      * `/<view>`  
      The directory holding the images for a context view

All provided images must be `PNG` files.

## How to use javascript

Inline javascript must be avoided, and all javascript for a page is implemented in its own file as a javascript ES-6 module.

The root directory for all javascript files is `src/main/resources/static/resources/js` where the javascript files are structured in subdirectories depending on the
context they are used for.

* `/`
  The directory for commonly used javascript modules, which can be used in any page and other javascript modules.
* `/fragments`  
  The directory for javascript modules used by the view components.
* `/basic/<mp_spec>`
  The directory for all basic examples javascript modules of a specification.
* `/advanced/<mp_spec>`
  The directory for all advanced examples javascript modules of a specification.

Javascript modules are implement at least in the following way.

```javascript
// Optional state object
const state = {
    attribute: value,
};

// Optional (e.g.: click event listener registration)
const registerElementClickEventListener = (options) => {
    const {
        element,
    } = options;
    element.addEventListener('click', () => console.log('element clicked'));
};

const init = (options) => {
};

export default {
    init,
};
```

Parameters for the javascript module are always provided as an options object. If html elements are used in any ways, then the html element instance itself is
provided and never ids.

Javascript modules are used in a template the following way.

```html
<!-- Inserts the contained tags at the end of the HTML page -->
<th:block layout:fragment="bottom">
    <!-- The used javascript is a javascript module -->
    <script type="module" th:inline="javascript">
        // '[[${pathHelper.buildResourcePath('/js/mp.js')}]]' is a javascript inlining expression which gets
        // resolved to the quoted string '/cditraining/resources/js/mp.js', so it carries its own quotes
        // Imports your modules
        import mp from [[${pathHelper.buildResourcePath('/js/mp.js')}]]
        import myModule from [[${pathHelper.buildResourcePath('/js/basic/<MP_SPEC>/myModule.js')}]]

        // Initializes your javascript module once per page load
        mp.registerOnLoad(() => myModule.init({
            ...
        }));
    </script>
</th:block>
```

