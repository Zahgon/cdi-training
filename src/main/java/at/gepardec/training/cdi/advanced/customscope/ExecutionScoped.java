package at.gepardec.training.cdi.advanced.customscope;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * The custom scope which is a normal scope (proxyable).
 * <p>
 * CDI declared "this annotation is a scope" with the {@code @NormalScope} meta-annotation and let the container find the
 * matching {@code Context}. Spring has no scope meta-annotation: a custom scope is a plain {@link Scope} referring to a
 * scope <em>name</em> that is registered on the bean factory, which {@link ExecutionContextExtension} does under
 * {@value ExecutionContextExtension#SCOPE_NAME}. Both strings must stay in sync.
 * <p>
 * The "normal scope", i.e. proxyable, half of the CDI semantics is expressed by {@link ScopedProxyMode#TARGET_CLASS}:
 * the injectable reference becomes a client proxy that resolves the contextual instance through this scope on every
 * invocation, which is exactly what a CDI normal-scoped client proxy did.
 */
@Scope(scopeName = "execution", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ExecutionScoped {
}
