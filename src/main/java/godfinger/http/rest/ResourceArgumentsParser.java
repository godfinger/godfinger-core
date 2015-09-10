package godfinger.http.rest;

import java.util.List;

import godfinger.http.rest.parameter.ResourceArgumentParser;

public class ResourceArgumentsParser {

  private final List<ResourceArgumentParser> argumentParsers;

  public ResourceArgumentsParser(List<ResourceArgumentParser> parameterParsers) {
    this.argumentParsers = parameterParsers;
  }

  public Object[] parse(InvocationContext context) throws Exception {
    if (argumentParsers.isEmpty()) {
      return null;
    }

    Object[] returnVal = new Object[argumentParsers.size()];
    for (int i = 0; i < argumentParsers.size(); i++) {
      ResourceArgumentParser argumentParser = argumentParsers.get(i);
      returnVal[i] = argumentParser.parse(context);
    }

    return returnVal;
  }

}
