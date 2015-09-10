package godfinger.http.rest;

import java.lang.annotation.Annotation;

public interface Interceptor {

  Class<? extends Annotation> annotationType();

  void beforeInvocation(InvocationContext context) throws Exception;

  void afterInvocation(InvocationContext context) throws Exception;

}
