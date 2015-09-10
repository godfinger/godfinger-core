package godfinger.http.rest.exception;

public class ParameterNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1114259589914290429L;

	public ParameterNotFoundException(String type, String name) {
		super("type=" + type + ", name=" + name);
	}

}
