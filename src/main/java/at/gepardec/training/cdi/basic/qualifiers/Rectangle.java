package at.gepardec.training.cdi.basic.qualifiers;

/**
 * The CDI version of this class was {@code @Dependent @Alternative}. An alternative only
 * becomes a bean once it is enabled in {@code beans.xml}, which never happens here, so
 * Rectangle is not a bean at all and every {@link Shape} injection point resolves to
 * {@link Circle}.
 * <p>
 * The Spring equivalent of a disabled alternative is a class carrying no stereotype: it is
 * never registered, so it can never be injected. Do not add {@code @Component} here - Spring
 * would then have two {@link Shape} candidates and resolve them by field name, which would
 * silently hand {@code QualifiersController.rectangle} a Rectangle and make the exercise
 * disappear.
 */
//Why do you think this is necessary?
public class Rectangle extends Shape {

}
