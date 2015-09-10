package godfinger.http.rest.parameter;

import godfinger.http.rest.InvocationContext;
import godfinger.http.rest.exception.ParameterNotFoundException;

public class ContextParameterParser implements ResourceArgumentParser {

	private final String name;
	private final boolean optional;

	public ContextParameterParser(String name, boolean optional) {
		this.name = name;
		this.optional = optional;
	}

	@Override
	public Object parse(InvocationContext context) {
		Object value = context.get(name);
		if (value != null) {
      return value;
    }

    if (optional) {
      return null;
    }

    throw new ParameterNotFoundException("@Context", name);
	}

}
