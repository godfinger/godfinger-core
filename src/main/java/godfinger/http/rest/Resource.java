package godfinger.http.rest;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import godfinger.http.HttpResponseFactory;
import io.netty.handler.codec.http.HttpMethod;

public class Resource {

	private final HttpMethod httpMethod;
	private final String pathString;
	private final Pattern pathPattern;
	private final Object object;
	private final Method method;
	private final ResourceArgumentsParser argumentsParser;
	private final HttpResponseFactory httpResponseFactory;
	private final List<Interceptor> interceptors;

	public Resource(HttpMethod httpMethod, String pathString, Object object, Method method, ResourceArgumentsParser argumentsParser, HttpResponseFactory httpResponseFactory, List<Interceptor> interceptors) {
		if (pathString.contains("{")) {
			pathString = pathString.replaceAll("\\{[A-Za-z0-9_.-]+\\}", "[A-Za-z0-9_.-]+");
			pathPattern = Pattern.compile(pathString);
		} else {
			pathPattern = null;
		}
		this.httpMethod = httpMethod;
		this.pathString = pathString;
		this.object = object;
		this.method = method;
		this.argumentsParser = argumentsParser;
		this.httpResponseFactory = httpResponseFactory;
		this.interceptors = interceptors;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public String getPathString() {
		return pathString;
	}

	public Object getObject() {
		return object;
	}

	public Method getMethod() {
		return method;
	}

	public ResourceArgumentsParser getArgumentsParser() {
		return argumentsParser;
	}

	public HttpResponseFactory getHttpResponseFactory() {
		return httpResponseFactory;
	}

	public List<Interceptor> getInterceptors() {
		return interceptors;
	}

	public boolean matches(HttpMethod httpMethod, String path) {
		if (!this.httpMethod.equals(httpMethod)) {
			return false;
		}

		if (pathPattern == null) {
			return pathString.equals(path);
		}

		Matcher matcher = pathPattern.matcher(path);
		return matcher.matches();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Resource [httpMethod=");
		builder.append(httpMethod);
		builder.append(", pathString=");
		builder.append(pathString);
		builder.append(", object=");
		builder.append(object);
		builder.append(", method=");
		builder.append(method);
		builder.append(", argumentsParser=");
		builder.append(argumentsParser);
		builder.append(", httpResponseFactory=");
		builder.append(httpResponseFactory);
		builder.append(", interceptors=");
		builder.append(interceptors);
		builder.append("]");
		return builder.toString();
	}

}
