package godfinger.http.rest.exception;

public class ForbiddenException extends InvocationRejectedException {

  public ForbiddenException(String message) {
    super(message, 403);
  }

}
