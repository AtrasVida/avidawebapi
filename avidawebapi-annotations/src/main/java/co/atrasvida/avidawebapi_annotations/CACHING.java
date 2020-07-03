package co.atrasvida.avidawebapi_annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;



@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface CACHING {
    CachSetting value();
}
