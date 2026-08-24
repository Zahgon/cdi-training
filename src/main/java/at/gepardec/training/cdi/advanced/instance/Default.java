package at.gepardec.training.cdi.advanced.instance;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * The Spring stand-in for {@code jakarta.enterprise.inject.Default}.
 *
 * <p>CDI applies {@code @Default} implicitly to every bean that declares no other qualifier and to
 * every injection point that names no qualifier, which is why {@code @Inject Instance<X>} sees only
 * the unqualified beans while {@code @Inject @Any Instance<X>} sees all of them. Spring has no
 * implicit qualifier: an unqualified {@code ObjectProvider} streams every candidate. Declaring the
 * qualifier explicitly on both the bean and the injection point is what restores the distinction.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Inherited
public @interface Default {
}
