package godfinger.http.rest.exception;

import io.netty.handler.codec.http.HttpMethod;

public class ResourceNotFound extends RuntimeException {
  public ResourceNotFound(HttpMethod method, String uri) {}
}
