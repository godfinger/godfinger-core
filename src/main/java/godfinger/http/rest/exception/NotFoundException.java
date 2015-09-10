package godfinger.http.rest.exception;

public class NotFoundException extends Exception {

  public NotFoundException(String resourceUrl) {
    super(resourceUrl);
  }

}
