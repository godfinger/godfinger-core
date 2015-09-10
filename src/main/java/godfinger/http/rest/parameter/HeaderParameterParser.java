package godfinger.http.rest.parameter;

import godfinger.http.rest.InvocationContext;
import godfinger.http.rest.exception.ParameterNotFoundException;
import godfinger.util.cast.LexicalCaster;
import io.netty.handler.codec.http.FullHttpRequest;

public class HeaderParameterParser implements ResourceArgumentParser {

	private final String name;
	private final LexicalCaster<?> lexicalCaster;
	private final boolean optional;
	private final String defaultValue;

	public HeaderParameterParser(String name, LexicalCaster<?> lexicalCaster, boolean optional, String defaultValue) {
		this.name = name;
		this.lexicalCaster = lexicalCaster;
		this.optional = optional;
		this.defaultValue = defaultValue;
	}

	@Override
	public Object parse(InvocationContext context) {
    FullHttpRequest request = context.getRequest();
    String value = request.headers().get(name);
		if (value == null) {
			if (optional) {
				return defaultValue;
			} else {
				throw new ParameterNotFoundException("@Header", name);
			}
		}
		return lexicalCaster.cast(value);
	}

}
