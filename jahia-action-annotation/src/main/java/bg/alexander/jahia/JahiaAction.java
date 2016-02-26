package bg.alexander.jahia;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jahia.bin.Action;
import org.springframework.stereotype.Component;

/**
 * Annotation qui permet au Spring de récuperer
 * les beans controlleurs et de les configurer avec
 * les parametres necessaires.
 *
 * {@link Action}
 * @author KIRILOV Alexandre
 *
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JahiaAction{
    /**
     * Le nom du controlleur utilisé par Jahia.
     * Default - le nom de la class
     * ex: si on met pas le nom
     * class MyController; name: myController
     *
     * @see Action#setName(String)
     */
    String name() default "";
    /**
     * La variable requireAuthenticatedUser utilisé
     * par Jahia. Default - false
     *
     * @see Action#setRequireAuthenticatedUser(boolean)
     */
    boolean requireAuthenticatedUser() default false;
}
