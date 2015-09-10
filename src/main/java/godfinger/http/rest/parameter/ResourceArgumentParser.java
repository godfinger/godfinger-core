package godfinger.http.rest.parameter;

import godfinger.http.rest.InvocationContext;

public interface ResourceArgumentParser {

  Object parse(InvocationContext context) throws Exception;

}
