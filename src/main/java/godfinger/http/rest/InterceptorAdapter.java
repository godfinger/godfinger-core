package godfinger.http.rest;

import java.lang.annotation.Annotation;

public abstract class InterceptorAdapter implements Interceptor {

  @Override
  public Class<? extends Annotation> annotationType() {
    return null;
  }

  @Override
  public void beforeInvocation(InvocationContext context) throws Exception {}

  @Override
  public void afterInvocation(InvocationContext context) throws Exception {}

}
