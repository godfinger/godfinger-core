package godfinger.http.rest.exception;

public class UnauthorizedException extends InvocationRejectedException {

  public UnauthorizedException(String message) {
    super(message, 401);
  }

}
