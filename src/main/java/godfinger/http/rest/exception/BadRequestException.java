package godfinger.http.rest.exception;

import java.util.Map;

public class BadRequestException extends InvocationFailedException {

  public BadRequestException(Map<String, String> errors) {
    super(errors);
  }

}
