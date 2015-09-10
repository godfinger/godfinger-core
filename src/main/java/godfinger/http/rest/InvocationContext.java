package godfinger.http.rest;

import java.util.HashMap;
import java.util.Map;

import io.netty.handler.codec.http.FullHttpRequest;

public class InvocationContext {

  private final Resource resource;
  private final FullHttpRequest request;
  private Map<Object, Object> keyValues;
  private Object[] arguments;
  private boolean aborted;
  private Object result;

  public InvocationContext(Resource resource, FullHttpRequest request) {
    this.resource = resource;
    this.request = request;
  }

  public Resource getResource() {
    return resource;
  }

  public FullHttpRequest getRequest() {
    return request;
  }

  public Object[] getArguments() {
    return arguments;
  }

  void setArguments(Object[] arguments) {
    this.arguments = arguments;
  }

  public Object get(Object key) {
    if (keyValues == null) {
      return null;
    }
    return keyValues.get(key);
  }

  public void set(Object key, Object value) {
    if (keyValues == null) {
      keyValues = new HashMap<>();
    }
    keyValues.put(key, value);
  }

  public void abortAndReturn(Object result) {
    this.result = result;
  }

  boolean isAborted() {
    return aborted;
  }

  public Object getResult() {
    return result;
  }

  void setResult(Object result) {
    this.result = result;
  }

}
