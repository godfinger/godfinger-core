package godfinger.http.rest.exception;

import java.util.Map;

public class InvocationFailedException extends Exception {

  private final Map<String, String> errors;

  public InvocationFailedException(Map<String, String> errors) {
    super(errors.toString());
    this.errors = errors;
  }

  public Map<String, String> getErrors() {
    return errors;
  }

}
