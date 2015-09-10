package godfinger.http.rest.exception;

import godfinger.http.rest.Resource;

public class DuplicateResourceException extends RuntimeException {

	private static final long serialVersionUID = -5641138355063979350L;

	public DuplicateResourceException(Resource firstResource, Resource secondResource) {
		super(createMessage(firstResource, secondResource));
	}

	private static String createMessage(Resource firstResource, Resource secondResource) {
		String resourceKey = firstResource.getHttpMethod().name() + " " + firstResource.getPathString();
		return resourceKey + " => " + firstResource.getMethod() + ", " + secondResource.getMethod();
	}

}
