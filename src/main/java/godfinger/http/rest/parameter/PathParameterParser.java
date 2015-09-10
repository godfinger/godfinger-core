package godfinger.http.rest.parameter;

import godfinger.http.rest.InvocationContext;
import godfinger.http.rest.exception.ParameterNotFoundException;
import godfinger.util.UriUtil;
import godfinger.util.cast.LexicalCaster;
import io.netty.handler.codec.http.FullHttpRequest;

public class PathParameterParser implements ResourceArgumentParser {

  private final String name;
  private final LexicalCaster<?> lexicalCaster;
  private final boolean optional;
  private final int directoryOrdinal;
  private final String defaultValue;

  public PathParameterParser(String name, String pathString, LexicalCaster<?> lexicalCaster, boolean optional,
          String defaultValue) {
    this.name = name;
    this.lexicalCaster = lexicalCaster;
    this.optional = optional;
    this.defaultValue = defaultValue;
    directoryOrdinal = getDirectoryOrdinal(pathString, name);
  }

  @Override
  public Object parse(InvocationContext context) {
    FullHttpRequest request = context.getRequest();
    String path = UriUtil.decodePath(request.getUri());
    String value = getParameter(path);
    if (value == null) {
      if (optional) {
        return defaultValue;
      } else {
        throw new ParameterNotFoundException("@Path", name);
      }
    }

    return lexicalCaster.cast(value);
  }

  private String getParameter(String path) {
    int occurrencesOfSlash = 0;
    for (int i = 0; i < path.length(); i++) {
      char ch = path.charAt(i);
      if (ch == '/') {
        if (directoryOrdinal == occurrencesOfSlash) {
          int indexOfNextSlash = path.indexOf('/', i + 1);
          if (indexOfNextSlash == -1) {
            indexOfNextSlash = path.indexOf('?', i + 1);
            if (indexOfNextSlash == -1) {
              indexOfNextSlash = path.length();
            }
          }
          return path.substring(i + 1, indexOfNextSlash);
        }
        occurrencesOfSlash++;
      }
    }
    return null;
  }

  private int getDirectoryOrdinal(String pathString, String name) {
    String[] pathSegments = pathString.substring(1).split("/");
    for (int i = 0; i < pathSegments.length; i++) {
      String pathSegment = pathSegments[i];
      if (pathSegment.equals("{" + name + "}")) {
        return i;
      }
    }
    throw new ParameterNotFoundException("@Path", name);
  }

}
