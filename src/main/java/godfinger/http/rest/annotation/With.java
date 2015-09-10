package godfinger.http.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import godfinger.http.rest.Interceptor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface With {

  Class<? extends Interceptor>[] value();

}
