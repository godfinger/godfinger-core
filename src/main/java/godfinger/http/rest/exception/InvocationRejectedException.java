package godfinger.http.rest.exception;

public class InvocationRejectedException extends Exception {

  private final Integer code;
  private final Object data;

  public InvocationRejectedException(String message) {
    this(message, null, null);
  }

  public InvocationRejectedException(String message, Integer code) {
    this(message, code, null);
  }

  public InvocationRejectedException(String message, Integer code, Object data) {
    super(message);
    this.code = code;
    this.data = data;
  }

  public Integer getCode() {
    return code;
  }

  public Object getData() {
    return data;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
    sb.append("{code=").append(code);
    sb.append(", message=").append(getMessage());
    if (data != null) {
      sb.append(", data=").append(data);
    }
    sb.append('}');
    return sb.toString();
  }

}
